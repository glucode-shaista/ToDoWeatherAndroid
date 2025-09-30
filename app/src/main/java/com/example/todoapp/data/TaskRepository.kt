package com.example.todoapp.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    //Flow<List<Task>> - live updates on UI
    //Flows from Room
    val allTasks: Flow<List<Task>> = dao.getAllTasks()
    val completedTasks: Flow<List<Task>> = dao.getCompletedTasks()
    val incompleteTasks: Flow<List<Task>> = dao.getIncompleteTasks()
    val favoriteTasks: Flow<List<Task>> = dao.getFavoriteTasks()

    fun searchTasks(query: String): Flow<List<Task>> {
        return dao.searchTasks(query)
    }

    //Function that can be paused and resumed, without blocking thread its running on. Asynchronous programming
    suspend fun createTask(task: Task) = dao.createTask(task)
    suspend fun updateTask(task: Task) = dao.updateTask(task)
    suspend fun deleteTask(task: Task) = dao.deleteTask(task)

}