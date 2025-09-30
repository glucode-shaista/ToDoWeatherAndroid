package com.example.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todoapp.ui.AddTaskScreen
import com.example.todoapp.ui.TaskListScreen
import com.example.todoapp.ui.EditTaskScreen
import com.example.todoapp.viewmodel.WeatherViewModel
import com.example.todoapp.viewmodel.TaskViewModel


//Manages screen transitions and navigation logic.
@Composable
fun AppNavGraph(
    navController:  NavHostController,
    weatherViewModel: WeatherViewModel,
    taskViewModel: TaskViewModel ) {
    //Hosts all navigation routes
    NavHost(
        //Manage screen transitions
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(route = Screen.TaskList.route) {
            TaskListScreen(
                viewModel = taskViewModel,
                weatherViewModel = weatherViewModel,
                onEditClick = { task ->
                navController.navigate(Screen.EditTask.createRoute(task.id)) //taskId, get specific task
                },
                onAddClick = {
                    navController.navigate(Screen.AddTask.route)
                }
            )
        }

        composable(route = Screen.AddTask.route) {
            AddTaskScreen(
                viewModel = taskViewModel,
                onSave = { task ->
                    taskViewModel.createTask(task)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            //Safely collect the latest tasks from StateFlow
            val tasks by taskViewModel.allTasks.collectAsState()
            //Can find the task safely
            val task = remember(tasks) {
                tasks.find { it.id == taskId}
            }

            if (task != null) {
                EditTaskScreen(
                    task = task,
                    onSaveClick = { updatedTask ->
                        taskViewModel.updateTask(updatedTask)
                        navController.popBackStack()
                    },
                    onCancelClick = {
                        navController.popBackStack()
                    }
                )
            } else {
                //Handle task not found, navigate back.
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

        }

}