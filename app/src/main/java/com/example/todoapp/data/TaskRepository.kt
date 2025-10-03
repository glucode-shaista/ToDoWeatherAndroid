package com.example.todoapp.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) : TaskRepositoryInterface {

    //Flow<List<Task>> - live updates on UI
    //Flows from Room
    override val allTasks: Flow<List<Task>> = dao.getAllTasks()
    override val completedTasks: Flow<List<Task>> = dao.getCompletedTasks()
    override val incompleteTasks: Flow<List<Task>> = dao.getIncompleteTasks()
    override val favoriteTasks: Flow<List<Task>> = dao.getFavoriteTasks()

    // Category-based flows
    override fun getTasksByCategory(category: Category): Flow<List<Task>> = dao.getTasksByCategory(category)
    override fun getIncompleteTasksByCategory(category: Category): Flow<List<Task>> = dao.getIncompleteTasksByCategory(category)
    override fun getCompletedTasksByCategory(category: Category): Flow<List<Task>> = dao.getCompletedTasksByCategory(category)

    // Priority-based flows
    override fun getTasksByPriority(priority: Priority): Flow<List<Task>> = dao.getTasksByPriority(priority)
    override fun getIncompleteTasksByPriority(priority: Priority): Flow<List<Task>> = dao.getIncompleteTasksByPriority(priority)

    // Combined filtering
    override fun getTasksByCategoryAndPriority(category: Category, priority: Priority): Flow<List<Task>> = 
        dao.getTasksByCategoryAndPriority(category, priority)
    override fun getIncompleteTasksByCategoryAndPriority(category: Category, priority: Priority): Flow<List<Task>> = 
        dao.getIncompleteTasksByCategoryAndPriority(category, priority)

    // Date-based flows
    override val todayTasks: Flow<List<Task>> = dao.getTodayTasks()
    override val overdueTasks: Flow<List<Task>> = dao.getOverdueTasks()

    // Search
    override fun searchTasks(query: String): Flow<List<Task>> {
        return dao.searchTasks(query)
    }

    //Function that can be paused and resumed, without blocking thread its running on. Asynchronous programming
    override suspend fun createTask(task: Task) = dao.createTask(task)
    override suspend fun updateTask(task: Task) = dao.updateTask(task)
    override suspend fun deleteTask(task: Task) = dao.deleteTask(task)
}
