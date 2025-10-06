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
import com.example.todoapp.ui.OnboardingScreen
import com.example.todoapp.viewmodel.WeatherViewModel
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.data.Task
import com.example.todoapp.data.OnboardingManagerInterface
import java.time.LocalDateTime

//Manages screen transitions and navigation logic.
@Composable
fun AppNavGraph(
    navController:  NavHostController,
    weatherViewModel: WeatherViewModel,
    taskViewModel: TaskViewModel,
    onboardingManager: OnboardingManagerInterface,
    onRequestLocationPermission: () -> Unit = {}
) {
    // Determine start destination based on onboarding status
    val startDestination = if (onboardingManager.isOnboardingCompleted()) {
        Screen.TaskList.route
    } else {
        Screen.Onboarding.route
    }
    
    //Hosts all navigation routes
    NavHost(
        //Manage screen transitions
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding screen
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    onboardingManager.setOnboardingCompleted()
                    navController.navigate(Screen.TaskList.route) {
                        // Clear the onboarding screen from back stack
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                },
                onRequestLocationPermission = onRequestLocationPermission
            )
        }
        composable(route = Screen.TaskList.route) {
            // Load weather when entering main app (after onboarding or on subsequent launches)
            LaunchedEffect(Unit) {
                weatherViewModel.loadWeatherForCurrentLocation()
            }
            
            TaskListScreen(
                viewModel = taskViewModel,
                weatherViewModel = weatherViewModel,
                onEditClick = { task ->
                navController.navigate(Screen.EditTask.createRoute(task.id)) //taskId, get specific task
                },
                onAddClick = {
                    navController.navigate(Screen.AddTask.route)
                },
                onRequestLocationPermission = onRequestLocationPermission
            )
        }

        composable(route = Screen.AddTask.route) {
            AddTaskScreen(
                onSave = { title, description, priority, category, dueDateTime ->
                    val newTask = Task(
                        id = 0, // Room will auto-generate
                        title = title,
                        description = description,
                        priority = priority,
                        category = category,
                        dueDateTime = dueDateTime,
                        createdDateTime = LocalDateTime.now(),
                        isCompleted = false,
                        favorite = false
                    )
                    taskViewModel.createTask(newTask)
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
                    onSave = { title, description, priority, category, dueDateTime ->
                        taskViewModel.updateTask(task.copy(
                            title = title,
                            description = description,
                            priority = priority,
                            category = category,
                            dueDateTime = dueDateTime
                        ))
                        navController.popBackStack()
                    },
                    onCancel = {
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