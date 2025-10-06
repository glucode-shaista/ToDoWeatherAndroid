package com.example.todoapp.viewmodel

@Deprecated("Replaced by Hilt-injected ViewModel")
class TaskViewModelFactory

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.todoapp.data.TaskRepository

//Enables dependency injection of TaskRepo into TaskViewModel, not possible with ViewModelProvider
//class TaskViewModelFactory(
    //private val repository: TaskRepository) : ViewModelProvider.Factory {

    //override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            //@Suppress("UNCHECKED_CAST")
            //return TaskViewModel(repository) as T
        //}
        //throw IllegalArgumentException("Unknown ViewModel class") //Prevents silent errors
    //}
//}