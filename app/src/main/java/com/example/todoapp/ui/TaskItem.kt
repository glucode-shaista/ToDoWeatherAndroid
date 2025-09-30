package com.example.todoapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import com.example.todoapp.data.Priority
import com.example.todoapp.data.Task

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit

) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)){

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (task.favorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favorite",
                        tint = if (task.favorite) Color(0xFFFFC107) else Color.Gray
                    )
                }
            }

            if (task.description.isNotBlank()) {

                Text(
                    text = task.description,
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = task.currentDateTime?.format(formatter) ?: "No Date",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = when (task.priority) {
                                    Priority.High -> Color.Red
                                    Priority.Medium -> Color(0xFFFFA000)
                                    Priority.Low -> Color.Blue
                                },
                                shape = CircleShape
                            )
                    )

                Text(
                    text = "Priority: ${task.priority.name}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 6.dp)
                    )
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = onCheckedChange
                    )
                    Text(
                        text = if (task.isCompleted) "Completed" else "Mark Complete",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskItem() {
    val sampleTask = Task(
        id = 1,
        title = "Do washing",
        description = "All white items",
        isCompleted = false,
        favorite = true,
        currentDateTime = java.time.LocalDateTime.now(),
        priority = Priority.Low
    )

    TaskItem(
        task = sampleTask,
        onCheckedChange = {},
        onFavoriteClick = {},
        onDeleteClick = {},
        onEditClick = {}
    )
}