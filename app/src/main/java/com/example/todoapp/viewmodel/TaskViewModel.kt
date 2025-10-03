package com.example.todoapp.viewmodel

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.todoapp.data.*
import com.example.todoapp.ui.TaskFilter
import com.example.todoapp.ui.TaskStatus
import com.example.todoapp.ui.DateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

//Acts as bridge between UI and Repository, manages business logic and state
@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepositoryInterface): ViewModel() {
    
    // Filter state
    private val _currentFilter = MutableStateFlow(TaskFilter())
    val currentFilter: StateFlow<TaskFilter> = _currentFilter.asStateFlow()

    // Filtered tasks based on current filter
    val filteredTasks: StateFlow<List<Task>> = combine(
        _currentFilter,
        repository.allTasks
    ) { filter, allTasks ->
        applyFilter(allTasks, filter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //Observing task lists from the repository (keeping for backward compatibility)
    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks: StateFlow<List<Task>> = repository.completedTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incompleteTasks: StateFlow<List<Task>> = repository.incompleteTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTasks: StateFlow<List<Task>> = repository.favoriteTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Quick access to specific filtered lists
    val todayTasks: StateFlow<List<Task>> = repository.todayTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overdueTasks: StateFlow<List<Task>> = repository.overdueTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //MutableStateFlow so can bind to searchbar
    private val _searchResults = MutableStateFlow<List<Task>>(emptyList())
    val searchResults: StateFlow<List<Task>> = _searchResults.asStateFlow()

    // Filter functions
    fun updateFilter(newFilter: TaskFilter) {
        _currentFilter.value = newFilter
    }

    fun clearFilters() {
        _currentFilter.value = TaskFilter()
    }

    private fun applyFilter(tasks: List<Task>, filter: TaskFilter): List<Task> {
        return tasks.filter { task ->
            // Search query filter (always AND - must match if specified)
            val searchMatch = if (filter.searchQuery.isBlank()) {
                true
            } else {
                task.title.contains(filter.searchQuery, ignoreCase = true) ||
                task.description.contains(filter.searchQuery, ignoreCase = true)
            }
            
            // If search doesn't match, exclude the task
            if (!searchMatch) return@filter false
            
            // Collect all active filter conditions (OR logic between different filter types)
            val activeFilters = mutableListOf<Boolean>()
            
            // Category filter
            filter.category?.let { selectedCategory ->
                activeFilters.add(task.category == selectedCategory)
            }
            
            // Priority filter
            filter.priority?.let { selectedPriority ->
                activeFilters.add(task.priority == selectedPriority)
            }
            
            // Status filter (only add if not ALL)
            if (filter.status != TaskStatus.ALL) {
                val statusMatch = when (filter.status) {
                    TaskStatus.PENDING -> !task.isCompleted
                    TaskStatus.COMPLETED -> task.isCompleted
                    TaskStatus.FAVORITES -> task.favorite
                    TaskStatus.ALL -> true // This case won't be reached due to the if condition
                }
                activeFilters.add(statusMatch)
            }
            
            // Date filter (only add if not ALL)
            if (filter.dateFilter != DateFilter.ALL) {
                val dateMatch = when (filter.dateFilter) {
                    DateFilter.TODAY -> {
                        task.dueDateTime?.let { taskDate ->
                            val today = LocalDateTime.now()
                            taskDate.toLocalDate() == today.toLocalDate()
                        } ?: false
                    }
                    DateFilter.OVERDUE -> {
                        task.dueDateTime?.let { taskDate ->
                            val now = LocalDateTime.now()
                            taskDate.isBefore(now) && !task.isCompleted
                        } ?: false
                    }
                    DateFilter.ALL -> true // This case won't be reached due to the if condition
                }
                activeFilters.add(dateMatch)
            }
            
            // If no filters are active (all are set to "All"), show all tasks
            if (activeFilters.isEmpty()) {
                return@filter true
            }
            
            // OR logic: task matches if ANY of the active filters match
            activeFilters.any { it }
        }
    }

    //CRUD Functions
    @OptIn(UnstableApi::class)
    fun createTask(task: Task) = viewModelScope.launch {
        try {
            Log.d("TaskViewModel", "Creating task: $task")
            repository.createTask(task)
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Failed to create task: ${e.message}")
            e.printStackTrace()
            // TODO: Show user-friendly error message
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        try {
            Log.d("TaskViewModel", "Updating task: $task")
            repository.updateTask(task)
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Failed to update task: ${e.message}")
            e.printStackTrace()
            // TODO: Show user-friendly error message
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        try {
            Log.d("TaskViewModel", "Deleting task: $task")
            repository.deleteTask(task)
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Failed to delete task: ${e.message}")
            e.printStackTrace()
            // TODO: Show user-friendly error message
        }
    }

    fun searchTasks(query: String) = viewModelScope.launch {
        repository.searchTasks(query).collect {
            _searchResults.value = it
        }
    }

    fun getTaskById(taskId: Int): Task? {
        return allTasks.value.find { it.id == taskId}
    }
}