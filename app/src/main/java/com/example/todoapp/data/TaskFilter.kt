package com.example.todoapp.data

// Data class to represent current filter state
data class TaskFilter(
    val category: Category? = null,
    val priority: Priority? = null,
    val status: TaskStatus = TaskStatus.ALL,
    val dateFilter: DateFilter = DateFilter.ALL,
    val searchQuery: String = ""
)

enum class TaskStatus {
    ALL, PENDING, COMPLETED, FAVORITES
}

enum class DateFilter {
    ALL, TODAY, OVERDUE
}
