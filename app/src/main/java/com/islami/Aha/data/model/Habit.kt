package com.islami.Aha.data.model

data class Habit(
    val id: Int,
    val name: String,
    val icon: String, // Contoh: "💧"
    val description: String,
    val isCompleted: Boolean = false,
    val streak: Int = 0 // Untuk Phase 2 nanti
)