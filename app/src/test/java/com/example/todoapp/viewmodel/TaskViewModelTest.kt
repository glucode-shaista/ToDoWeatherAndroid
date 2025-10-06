package com.example.todoapp.viewmodel

import com.example.todoapp.data.*
import com.example.todoapp.ui.TaskFilter
import com.example.todoapp.ui.TaskStatus
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
class TaskViewModelTest {

    private lateinit var mockRepository: TaskRepositoryInterface
    private lateinit var viewModel: TaskViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        viewModel = TaskViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `viewModel can be created successfully`() = runTest {
        // Given: Mock repository
        
        // When: No action needed (ViewModel is created)
        
        // Then: ViewModel should be created
        assertNotNull(viewModel)
        assertNotNull(viewModel.filteredTasks)
        assertNotNull(viewModel.currentFilter)
    }

    @Test
    fun `clearFilters sets default values`() = runTest {
        // Given: Set some filters first
        val customFilter = TaskFilter(
            status = TaskStatus.COMPLETED,
            category = Category.WORK,
            priority = Priority.High,
            searchQuery = "test"
        )
        viewModel.updateFilter(customFilter)

        // When: Clear filters
        viewModel.clearFilters()

        // Then: Should have default values
        val currentFilter = viewModel.currentFilter.value
        assertEquals(TaskStatus.ALL, currentFilter.status)
        assertNull(currentFilter.category)
        assertNull(currentFilter.priority)
        assertTrue(currentFilter.searchQuery.isEmpty())
    }

    @Test
    fun `updateFilter updates current filter`() = runTest {
        // Given: Custom filter
        val newFilter = TaskFilter(
            status = TaskStatus.PENDING,
            category = Category.PERSONAL,
            priority = Priority.Medium,
            searchQuery = "search term"
        )

        // When: Update filter
        viewModel.updateFilter(newFilter)

        // Then: Current filter should be updated
        val currentFilter = viewModel.currentFilter.value
        assertEquals(TaskStatus.PENDING, currentFilter.status)
        assertEquals(Category.PERSONAL, currentFilter.category)
        assertEquals(Priority.Medium, currentFilter.priority)
        assertEquals("search term", currentFilter.searchQuery)
    }

    @Test
    fun `filter status options work correctly`() = runTest {
        // Test each status type can be set
        val pendingFilter = TaskFilter(status = TaskStatus.PENDING)
        val completedFilter = TaskFilter(status = TaskStatus.COMPLETED) 
        val favoritesFilter = TaskFilter(status = TaskStatus.FAVORITES)
        val allFilter = TaskFilter(status = TaskStatus.ALL)

        // When: Set different status filters
        viewModel.updateFilter(pendingFilter)
        assertEquals(TaskStatus.PENDING, viewModel.currentFilter.value.status)

        viewModel.updateFilter(completedFilter)
        assertEquals(TaskStatus.COMPLETED, viewModel.currentFilter.value.status)

        viewModel.updateFilter(favoritesFilter)
        assertEquals(TaskStatus.FAVORITES, viewModel.currentFilter.value.status)

        viewModel.updateFilter(allFilter)
        assertEquals(TaskStatus.ALL, viewModel.currentFilter.value.status)
    }

    @Test
    fun `category and priority can be filtered independently`() = runTest {
        // Given: Filter with just category
        val workFilter = TaskFilter(category = Category.WORK)
        viewModel.updateFilter(workFilter)
        assertEquals(Category.WORK, viewModel.currentFilter.value.category)
        assertNull(viewModel.currentFilter.value.priority)

        // Given: Filter with just priority  
        val highPriorityFilter = TaskFilter(priority = Priority.High)
        viewModel.updateFilter(highPriorityFilter)
        assertEquals(Priority.High, viewModel.currentFilter.value.priority)
        assertNull(viewModel.currentFilter.value.category)

        // Given: Filter with both
        val bothFilter = TaskFilter(category = Category.PERSONAL, priority = Priority.Low)
        viewModel.updateFilter(bothFilter)
        assertEquals(Category.PERSONAL, viewModel.currentFilter.value.category)
        assertEquals(Priority.Low, viewModel.currentFilter.value.priority)
    }

    @Test
    fun `search query functionality works`() = runTest {
        // Given: Search filter
        val searchFilter = TaskFilter(searchQuery = "important meeting")
        
        // When: Apply search
        viewModel.updateFilter(searchFilter)
        
        // Then: Search query should be stored
        assertEquals("important meeting", viewModel.currentFilter.value.searchQuery)
    }

    // Helper function to create test tasks
    private fun createTask(
        id: Int = 1,
        title: String = "Test Task") = Task(
        id = id,
        title = title,
        description = "Test Description",
        isCompleted = false,
        favorite = false,
        priority = Priority.Medium,
        category = Category.OTHER,
        dueDateTime = null,
        createdDateTime = LocalDateTime.now()
    )
}
