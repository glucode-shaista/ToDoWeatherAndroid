package com.example.todoapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.*
import com.example.todoapp.viewmodel.WeatherViewModel
import com.example.todoapp.viewmodel.TaskViewModel
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    weatherViewModel: WeatherViewModel,
    onEditClick: (Task) -> Unit,
    onAddClick: () -> Unit,
    onRequestLocationPermission: (() -> Unit)? = null
) {
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    // Check if any filters are active
    val hasActiveFilters = currentFilter != TaskFilter()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Tasks",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Filter Tasks",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // Active filter indicator
                        if (hasActiveFilters) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = Color(0xFFFF1744), // Bright red indicator
                                        shape = CircleShape
                                    )
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667eea), // Soft Blue
                            Color(0xFF764ba2), // Purple
                            Color(0xFFf093fb), // Light Pink
                            Color(0xFFf5576c)  // Coral
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Weather Section
                WeatherScreen(
                    weatherViewModel = weatherViewModel,
                    onRequestLocationPermission = onRequestLocationPermission
                )
                
                // Filter Section (collapsible with animation)
                AnimatedVisibility(
                    visible = showFilters,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        )
                    ) {
                        TaskFilterChips(
                            currentFilter = currentFilter,
                            onFilterChange = { newFilter ->
                                viewModel.updateFilter(newFilter)
                            },
                            onApplyFilters = {
                                showFilters = false // Close filter section when Apply is clicked
                            }
                        )
                    }
                }
                
                if (showFilters) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Task List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Active Tasks Section
                    val activeTasks = filteredTasks.filter { !it.isCompleted }
                    if (activeTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "To Do Tasks (${activeTasks.size})",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                            )
                        }
                        
                        items(activeTasks) { task ->
                            TaskItem(
                                task = task,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateTask(task.copy(isCompleted = isChecked))
                                },
                                onFavoriteClick = {
                                    viewModel.updateTask(task.copy(favorite = !task.favorite))
                                },
                                onDeleteClick = { viewModel.deleteTask(task) },
                                onEditClick = { onEditClick(task) }
                            )
                        }
                    }

                    // Completed Tasks Section
                    val completedTasks = filteredTasks.filter { it.isCompleted }
                    if (completedTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Completed Tasks (${completedTasks.size})",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                            )
                        }
                        
                        items(completedTasks) { task ->
                            TaskItem(
                                task = task,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateTask(task.copy(isCompleted = isChecked))
                                },
                                onFavoriteClick = {
                                    viewModel.updateTask(task.copy(favorite = !task.favorite))
                                },
                                onDeleteClick = { viewModel.deleteTask(task) },
                                onEditClick = { onEditClick(task) }
                            )
                        }
                    }

                    // Empty State
                    if (filteredTasks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (currentFilter == TaskFilter()) {
                                        "No Tasks yet. Start Adding one."
                                    } else {
                                        "No tasks match your current filters."
                                    },
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}