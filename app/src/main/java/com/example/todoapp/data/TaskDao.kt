package com.example.todoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//Data Access Object - how app communicates with DB
//Read/Write Operations
@Dao
//Marks interface as Room DAO, room will generate actual code for these methods.
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getIncompleteTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE favorite = 1 ORDER BY isCompleted ASC, CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getFavoriteTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' ORDER BY isCompleted ASC, CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun searchTasks(query: String): Flow<List<Task>>

    // Category filtering queries
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY isCompleted ASC, CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getTasksByCategory(category: Category): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category = :category AND isCompleted = 0 ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getIncompleteTasksByCategory(category: Category): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category = :category AND isCompleted = 1 ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getCompletedTasksByCategory(category: Category): Flow<List<Task>>

    // Priority filtering queries
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY isCompleted ASC, dueDateTime ASC, id ASC")
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE priority = :priority AND isCompleted = 0 ORDER BY dueDateTime ASC, id ASC")
    fun getIncompleteTasksByPriority(priority: Priority): Flow<List<Task>>

    // Combined filtering
    @Query("SELECT * FROM tasks WHERE category = :category AND priority = :priority ORDER BY isCompleted ASC, dueDateTime ASC, id ASC")
    fun getTasksByCategoryAndPriority(category: Category, priority: Priority): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category = :category AND priority = :priority AND isCompleted = 0 ORDER BY dueDateTime ASC, id ASC")
    fun getIncompleteTasksByCategoryAndPriority(category: Category, priority: Priority): Flow<List<Task>>

    // Date-based queries - updated to use dueDateTime
    @Query("SELECT * FROM tasks WHERE date(dueDateTime) = date('now') ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getTodayTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE date(dueDateTime) < date('now') AND isCompleted = 0 ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END, dueDateTime ASC, id ASC")
    fun getOverdueTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTask(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}