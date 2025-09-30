package com.example.todoapp.viewmodel

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


//Acts as bridge between UI and Repository, manages business logic and state
class TaskViewModel(private val repository: TaskRepository): ViewModel() {
    //Observing task lists from the repository
    //StateFlow - allows changes to show automatically in UI.
    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    //Converts state into StateFlow - auto recompose

    val completedTasks: StateFlow<List<Task>> = repository.completedTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incompleteTasks: StateFlow<List<Task>> = repository.incompleteTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTasks: StateFlow<List<Task>> = repository.favoriteTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //MutableStateFlow so can bind to searchbar
    private val _searchResults = MutableStateFlow<List<Task>>(emptyList())
    val searchResults: StateFlow<List<Task>> = _searchResults.asStateFlow()

    //CRUD Functions
    @OptIn(UnstableApi::class)
    fun createTask(task: Task) = viewModelScope.launch {
        Log.d("TaskViewModel", "Creating task: $task")
            repository.createTask(task)

    }

    fun updateTask(task: Task) = viewModelScope.launch {
            repository.updateTask(task)

    }

    fun deleteTask(task: Task) = viewModelScope.launch {
            repository.deleteTask(task)

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