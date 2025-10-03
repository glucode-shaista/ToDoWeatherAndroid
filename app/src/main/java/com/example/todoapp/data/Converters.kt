package com.example.todoapp.data

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Provides time converters for Room DB, to store complex data types, doesn't support by default
//Store tasks date, priority, and category cleanly in SQLite.
class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    //Converts to string to store in DB
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    //Converts Priority enum to string to save in DB.
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it, formatter)
        }
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return  priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }

    // Category converters
    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return Category.valueOf(value)
    }
}
