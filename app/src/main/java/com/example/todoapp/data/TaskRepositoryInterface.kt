package com.example.todoapp.data

import kotlinx.coroutines.flow.Flow

interface TaskRepositoryInterface {
    // Flow properties for live data
    val allTasks: Flow<List<Task>>
    val completedTasks: Flow<List<Task>>
    val incompleteTasks: Flow<List<Task>>
    val favoriteTasks: Flow<List<Task>>
    val todayTasks: Flow<List<Task>>
    val overdueTasks: Flow<List<Task>>

    // Category-based methods
    fun getTasksByCategory(category: Category): Flow<List<Task>>
    fun getIncompleteTasksByCategory(category: Category): Flow<List<Task>>
    fun getCompletedTasksByCategory(category: Category): Flow<List<Task>>

    // Priority-based methods
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>
    fun getIncompleteTasksByPriority(priority: Priority): Flow<List<Task>>

    // Combined filtering methods
    fun getTasksByCategoryAndPriority(category: Category, priority: Priority): Flow<List<Task>>
    fun getIncompleteTasksByCategoryAndPriority(category: Category, priority: Priority): Flow<List<Task>>

    // Search method
    fun searchTasks(query: String): Flow<List<Task>>

    // CRUD operations
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}
