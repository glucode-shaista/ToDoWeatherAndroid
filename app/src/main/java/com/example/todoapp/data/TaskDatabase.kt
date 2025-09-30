package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//tells Room what entities and versions to use and what main DB contains - Task entity
@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
//Inherits from Room DB
abstract class TaskDatabase : RoomDatabase() {
    //Gives Access to DAO
    abstract fun taskDao(): TaskDao

    //Setup DB Instance (Singleton)
    companion object {
        //Ensures updates to this reference are visible across threads.
        @Volatile
        private var INSTANCE: TaskDatabase? = null
        //Singleton pattern to avoid multiple DB instances/avoid memory leaks
        fun getDatabase(context: Context): TaskDatabase {
            //Checks if DB exists
            return INSTANCE ?: synchronized(this) {
                //Creates DB using Room builder
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).build()
                //Caches and returns new instance.
                INSTANCE = instance
                return instance
            }

        }
    }
}
