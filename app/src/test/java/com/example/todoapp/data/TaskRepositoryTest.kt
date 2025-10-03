package com.example.todoapp.data

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class TaskRepositoryTest {

    private lateinit var mockDao: TaskDao
    private lateinit var repository: TaskRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockDao = mockk(relaxed = true)
        repository = TaskRepository(mockDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `repository delegates CRUD operations to DAO`() = runTest {
        // Given
        val task = createTestTask(1, "Test Task")
        val updatedTask = task.copy(title = "Updated Task")

        // When: Create
        repository.createTask(task)
        
        // Then
        coVerify { mockDao.createTask(task) }

        // When: Update
        repository.updateTask(updatedTask)
        
        // Then
        coVerify { mockDao.updateTask(updatedTask) }

        // When: Delete
        repository.deleteTask(task)
        
        // Then
        coVerify { mockDao.deleteTask(task) }
    }

    @Test
    fun `basic flows delegate correctly to DAO`() = runTest {
        // Given
        val testTasks = listOf(
            createTestTask(1, "Task 1", isCompleted = true),
            createTestTask(2, "Task 2", favorite = true),
            createTestTask(3, "Task 3", isCompleted = false)
        )
        
        every { mockDao.getAllTasks() } returns flowOf(testTasks)
        every { mockDao.getCompletedTasks() } returns flowOf(testTasks.filter { it.isCompleted })
        every { mockDao.getIncompleteTasks() } returns flowOf(testTasks.filter { !it.isCompleted })
        every { mockDao.getFavoriteTasks() } returns flowOf(testTasks.filter { it.favorite })
        every { mockDao.getTodayTasks() } returns flowOf(testTasks.take(1))
        every { mockDao.getOverdueTasks() } returns flowOf(testTasks.take(1))

        // When & Then: Test all flows collect
        repository.allTasks.collect { }
        repository.completedTasks.collect { }
        repository.incompleteTasks.collect { }
        repository.favoriteTasks.collect { }
        repository.todayTasks.collect { }
        repository.overdueTasks.collect { }
        
        // Verify DAO calls
        verify { mockDao.getAllTasks() }
        verify { mockDao.getCompletedTasks() }
        verify { mockDao.getIncompleteTasks() }
        verify { mockDao.getFavoriteTasks() }
        verify { mockDao.getTodayTasks() }
        verify { mockDao.getOverdueTasks() }
    }

    @Test
    fun `category-based queries delegate correctly`() = runTest {
        // Given
        val category = Category.WORK
        val workTasks = listOf(
            createTestTask(1, "Work Task 1", category = category, isCompleted = false),
            createTestTask(2, "Work Task 2", category = category, isCompleted = true)
        )
        
        every { mockDao.getTasksByCategory(category) } returns flowOf(workTasks)
        every { mockDao.getIncompleteTasksByCategory(category) } returns flowOf(workTasks.filter { !it.isCompleted })
        every { mockDao.getCompletedTasksByCategory(category) } returns flowOf(workTasks.filter { it.isCompleted })

        // When
        repository.getTasksByCategory(category).collect { }
        repository.getIncompleteTasksByCategory(category).collect { }
        repository.getCompletedTasksByCategory(category).collect { }

        // Then
        verify { mockDao.getTasksByCategory(category) }
        verify { mockDao.getIncompleteTasksByCategory(category) }
        verify { mockDao.getCompletedTasksByCategory(category) }
    }

    @Test
    fun `priority-based queries delegate correctly`() = runTest {
        // Given
        val priority = Priority.High
        val highTasks = listOf(
            createTestTask(1, "High Task 1", priority = priority, isCompleted = false),
            createTestTask(2, "High Task 2", priority = priority, isCompleted = true)
        )
        
        every { mockDao.getTasksByPriority(priority) } returns flowOf(highTasks)
        every { mockDao.getIncompleteTasksByPriority(priority) } returns flowOf(highTasks.filter { !it.isCompleted })

        // When
        repository.getTasksByPriority(priority).collect { }
        repository.getIncompleteTasksByPriority(priority).collect { }

        // Then
        verify { mockDao.getTasksByPriority(priority) }
        verify { mockDao.getIncompleteTasksByPriority(priority) }
    }

    @Test
    fun `combined queries delegate correctly`() = runTest {
        // Given
        val category = Category.WORK
        val priority = Priority.High
        val combinedTasks = listOf(
            createTestTask(1, "Work High Task", category = category, priority = priority),
            createTestTask(2, "Work High Task 2", category = category, priority = priority)
        )
        
        every { mockDao.getTasksByCategoryAndPriority(category, priority) } returns flowOf(combinedTasks)
        every { mockDao.getIncompleteTasksByCategoryAndPriority(category, priority) } returns flowOf(combinedTasks.filter { !it.isCompleted })

        // When
        repository.getTasksByCategoryAndPriority(category, priority).collect { }
        repository.getIncompleteTasksByCategoryAndPriority(category, priority).collect { }

        // Then
        verify { mockDao.getTasksByCategoryAndPriority(category, priority) }
        verify { mockDao.getIncompleteTasksByCategoryAndPriority(category, priority) }
    }

    @Test
    fun `search queries delegate correctly`() = runTest {
        // Given
        val query = "meeting"
        val searchResults = listOf(
            createTestTask(1, "Team Meeting", description = "Important team meeting notes"),
            createTestTask(2, "Meeting Notes", description = "Weekly standup notes")
        )
        
        every { mockDao.searchTasks(query) } returns flowOf(searchResults)

        // When
        repository.searchTasks(query).collect { }

        // Then
        verify { mockDao.searchTasks(query) }
        
        // Verify the exact query was passed
        val captor = slot<String>()
        verify { mockDao.searchTasks(capture(captor)) }
        assertEquals(query, captor.captured)
    }

    @Test
    fun `repository implements TaskRepositoryInterface correctly`() = runTest {
        // This test ensures all interface methods are properly implemented
        val repositoryInterface: TaskRepositoryInterface = repository
        
        // Verify Flow properties are accessible
        assertNotNull(repositoryInterface.allTasks)
        assertNotNull(repositoryInterface.completedTasks)
        assertNotNull(repositoryInterface.incompleteTasks)
        assertNotNull(repositoryInterface.favoriteTasks)
        assertNotNull(repositoryInterface.todayTasks)
        assertNotNull(repositoryInterface.overdueTasks)

        // Verify Flow functions work
        val testCategory = Category.PERSONAL
        val testPriority = Priority.Medium
        
        assertNotNull(repositoryInterface.getTasksByCategory(testCategory))
        assertNotNull(repositoryInterface.getIncompleteTasksByCategory(testCategory))
        assertNotNull(repositoryInterface.getCompletedTasksByCategory(testCategory))
        assertNotNull(repositoryInterface.getTasksByPriority(testPriority))
        assertNotNull(repositoryInterface.getIncompleteTasksByPriority(testPriority))
        assertNotNull(repositoryInterface.getTasksByCategoryAndPriority(testCategory, testPriority))
        assertNotNull(repositoryInterface.getIncompleteTasksByCategoryAndPriority(testCategory, testPriority))
        assertNotNull(repositoryInterface.searchTasks("test"))
    }

    @Test
    fun `flow delegation works correctly`() = runTest {
        // Given
        val testTasks = listOf(
            createTestTask(1, "Task 1", category = Category.WORK, priority = Priority.High, favorite = true),
            createTestTask(2, "Task 2", category = Category.PERSONAL, priority = Priority.Low, favorite = false)
        )
        
        every { mockDao.getAllTasks() } returns flowOf(testTasks)

        // When: Ensure repository creates flow that delegates to DAO
        repository.allTasks.collect { }
        
        // Then: Verify DAO method was called
        verify { mockDao.getAllTasks() }
        assertNotNull(repository.allTasks)
    }

    // Helper function to create test tasks
    private fun createTestTask(
        id: Int = 1,
        title: String = "Test Task",
        description: String = "Test Description",
        isCompleted: Boolean = false,
        favorite: Boolean = false,
        priority: Priority = Priority.Medium,
        category: Category = Category.OTHER,
        dueDateTime: LocalDateTime? = null
    ) = Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        favorite = favorite,
        priority = priority,
        category = category,
        dueDateTime = dueDateTime,
        createdDateTime = LocalDateTime.now()
    )
}
