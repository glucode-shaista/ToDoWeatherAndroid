package com.example.todoapp.ui

import com.example.todoapp.data.Category
import com.example.todoapp.data.Priority

data class TaskFilter(
    val status: TaskStatus = TaskStatus.ALL,
    val category: Category? = null,
    val priority: Priority? = null,
    val dateFilter: DateFilter = DateFilter.ALL,
    val searchQuery: String = ""
)

enum class TaskStatus {
    ALL,
    PENDING,
    COMPLETED,
    FAVORITES
}

enum class DateFilter {
    ALL,
    TODAY,
    OVERDUE
}
