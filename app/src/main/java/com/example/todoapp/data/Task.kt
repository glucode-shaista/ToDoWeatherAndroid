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
    val currentDateTime: LocalDateTime? = LocalDateTime.now(),
    val isCompleted: Boolean = false, //Whether task is completed or not.
    val favorite: Boolean = false,
    val priority: Priority = Priority.Low

)
