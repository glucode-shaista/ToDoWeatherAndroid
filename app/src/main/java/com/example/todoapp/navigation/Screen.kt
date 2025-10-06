package com.example.todoapp.navigation


//AppNavGraph uses to refer to screens in type-safe and consistent way.
//Defines Routes in navigation
//Allows inheritance and to use functions
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object TaskList : Screen("task_list")
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Int): String = "edit_task/$taskId"
    }
}