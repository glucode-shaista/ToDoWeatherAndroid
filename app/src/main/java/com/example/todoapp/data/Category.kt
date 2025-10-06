package com.example.todoapp.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(
    val displayName: String,
    val icon: ImageVector,
    val colorName: String // For theming
) {
    WORK("Work", Icons.Filled.Work, "primary"),
    PERSONAL("Personal", Icons.Filled.Person, "secondary"),
    SHOPPING("Shopping", Icons.Filled.ShoppingCart, "tertiary"),
    HEALTH("Health", Icons.Filled.FavoriteBorder, "error"),
    FINANCE("Finance", Icons.Filled.AccountBalance, "primary"),
    EDUCATION("Education", Icons.Filled.School, "secondary"),
    HOME("Home", Icons.Filled.Home, "tertiary"),
    TRAVEL("Travel", Icons.Filled.Flight, "primary"),
    OTHER("Other", Icons.Filled.Category, "outline")
}
