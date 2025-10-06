package com.example.todoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterChips(
    currentFilter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    onApplyFilters: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Local state for pending filter changes
    var pendingFilter by remember(currentFilter) { mutableStateOf(currentFilter) }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .heightIn(max = 400.dp) // Limit max height
            .verticalScroll(rememberScrollState()) // Make it scrollable
    ) {
        // Search Bar
        OutlinedTextField(
            value = pendingFilter.searchQuery,
            onValueChange = { query ->
                pendingFilter = pendingFilter.copy(searchQuery = query)
            },
            label = { Text("Search tasks...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (pendingFilter.searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            pendingFilter = pendingFilter.copy(searchQuery = "")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Quick Actions Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Quick Filters",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            TextButton(
                onClick = { 
                    pendingFilter = TaskFilter() // Reset all filters
                }
            ) {
                Text("Clear All")
            }
        }

        // Status Filter Row
        Text(
            text = "Status",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(TaskStatus.values()) { status ->
                FilterChip(
                    onClick = { pendingFilter = pendingFilter.copy(status = status) },
                    label = { 
                        Text(
                            when(status) {
                                TaskStatus.ALL -> "All"
                                TaskStatus.PENDING -> "Pending"
                                TaskStatus.COMPLETED -> "Completed"
                                TaskStatus.FAVORITES -> "Favorites"
                            }
                        )
                    },
                    selected = pendingFilter.status == status,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Category Filter Row
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                FilterChip(
                    onClick = { pendingFilter = pendingFilter.copy(category = null) },
                    label = { Text("All") },
                    selected = pendingFilter.category == null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            items(Category.values()) { category ->
                FilterChip(
                    onClick = { pendingFilter = pendingFilter.copy(category = category) },
                    label = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (pendingFilter.category == category) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(category.displayName)
                        }
                    },
                    selected = pendingFilter.category == category,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Priority Filter Row
        Text(
            text = "Priority",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                FilterChip(
                    onClick = { pendingFilter = pendingFilter.copy(priority = null) },
                    label = { Text("All") },
                    selected = pendingFilter.priority == null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            items(Priority.values()) { priority ->
                FilterChip(
                    onClick = { pendingFilter = pendingFilter.copy(priority = priority) },
                    label = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Priority color indicator
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = when (priority) {
                                            Priority.High -> Color(0xFFFF1744)
                                            Priority.Medium -> Color(0xFFFF9800)
                                            Priority.Low -> Color(0xFF4CAF50)
                                        },
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(priority.name)
                        }
                    },
                    selected = pendingFilter.priority == priority,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Date Filter Row
        Text(
            text = "Date",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(DateFilter.values()) { dateFilter ->
                FilterChip(
                    onClick = { pendingFilter = pendingFilter.copy(dateFilter = dateFilter) },
                    label = { 
                        Text(
                            when(dateFilter) {
                                DateFilter.ALL -> "All"
                                DateFilter.TODAY -> "Today"
                                DateFilter.OVERDUE -> "Overdue"
                            }
                        )
                    },
                    selected = pendingFilter.dateFilter == dateFilter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Apply Filters Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), // Add some top padding
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    pendingFilter = TaskFilter() // Reset to default
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }
            
            Button(
                onClick = {
                    onFilterChange(pendingFilter)
                    onApplyFilters()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Apply Filters")
            }
        }
    }
}