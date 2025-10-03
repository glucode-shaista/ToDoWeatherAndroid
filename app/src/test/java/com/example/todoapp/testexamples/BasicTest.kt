package com.example.todoapp.testexamples

import com.example.todoapp.data.Task
import com.example.todoapp.data.Category
import com.example.todoapp.data.Priority
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class BasicTest {

    @Test
    fun `test task creation`() {
        // Given
        val taskTitle = "Test Task"
        val taskDescription = "Test Description"

        // When
        val task = Task(
            title = taskTitle,
            description = taskDescription,
            category = Category.WORK,
            priority = Priority.High,
            isCompleted = false,
            favorite = false,
            dueDateTime = LocalDateTime.now().plusDays(1),
            createdDateTime = LocalDateTime.now()
        )

        // Then
        assertEquals(taskTitle, task.title)
        assertEquals(taskDescription, task.description)
        assertEquals(Category.WORK, task.category)
        assertEquals(Priority.High, task.priority)
        assertFalse(task.isCompleted)
        assertFalse(task.favorite)
        assertNotNull(task.dueDateTime)
        assertNotNull(task.createdDateTime)
    }

    @Test
    fun `test category enum values`() {
        // Test all category values exist
        assertNotNull(Category.WORK)
        assertNotNull(Category.PERSONAL)
        assertNotNull(Category.SHOPPING)
        assertNotNull(Category.OTHER)

        // Test display names
        assertEquals("Work", Category.WORK.displayName)
        assertEquals("Personal", Category.PERSONAL.displayName)
        assertEquals("Shopping", Category.SHOPPING.displayName)
    }

    @Test
    fun `test priority enum values`() {
        // Test all priority values exist
        assertNotNull(Priority.High)
        assertNotNull(Priority.Medium)
        assertNotNull(Priority.Low)

        // Test comparison
        assertTrue(Priority.High != Priority.Low)
        assertTrue(Priority.Medium != Priority.High)
    }

    @Test
    fun `test task with different priorities`() {
        val highTask = createTaskWithPriority(Priority.High)
        val mediumTask = createTaskWithPriority(Priority.Medium)
        val lowTask = createTaskWithPriority(Priority.Low)

        assertEquals(Priority.High, highTask.priority)
        assertEquals(Priority.Medium, mediumTask.priority)
        assertEquals(Priority.Low, lowTask.priority)
    }

    private fun createTaskWithPriority(priority: Priority): Task {
        return Task(
            title = "Task with ${priority.name} priority",
            description = "Description",
            priority = priority,
            category = Category.WORK
        )
    }
}
