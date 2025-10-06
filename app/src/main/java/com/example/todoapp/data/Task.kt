package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

//Tells Room this class represents a table called tasks.
//Defines Data Model for a task and tells Room how to structure and store data in task table in local SQLite DB.
//Room Entity
@Entity(tableName = "tasks")
data class Task(
    //Auto-generates a unique ID for each task.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDateTime: LocalDateTime? = null, // When the task is due
    val createdDateTime: LocalDateTime = LocalDateTime.now(), // When task was created
    val isCompleted: Boolean = false, //Whether task is completed or not.
    val favorite: Boolean = false,
    val priority: Priority = Priority.Low,
    val category: Category = Category.OTHER // New field for categorization
) {
    // Backward compatibility - map currentDateTime to dueDateTime
    val currentDateTime: LocalDateTime?
        get() = dueDateTime
}