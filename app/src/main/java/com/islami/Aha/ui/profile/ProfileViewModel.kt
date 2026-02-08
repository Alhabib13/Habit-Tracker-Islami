package com.islami.Aha.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islami.Aha.data.local.HabitDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserInfo(
    val name: String = "Pengguna Lokal",
    val email: String = "",
    val avatarInitial: String = "P",
    val isLoggedIn: Boolean = false
)

data class Achievement(
    val id: String,
    val emoji: String,
    val name: String,
    val description: String,
    val isUnlocked: Boolean,
    val progress: Float
)

data class WeeklySummary(
    val completionPercentage: Float = 0f,
    val activeDays: Int = 0,
    val totalDays: Int = 7,
    val bestCategory: String = "-"
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userInfo: UserInfo = UserInfo(),
    val totalHabits: Int = 0,
    val totalCompleted: Int = 0,
    val currentStreak: Int = 0,
    val achievements: List<Achievement> = emptyList(),
    val weeklySummary: WeeklySummary = WeeklySummary(),
    val showLogoutConfirmation: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val habitDao: HabitDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            habitDao.getHabits().collect { habits ->
                val totalHabits = habits.size
                val totalCompletedToday = habits.count { it.isCompleted }
                val allCompleteToday = totalHabits > 0 && totalCompletedToday == totalHabits

                val categoryCompletions = habits.groupBy { it.category }
                    .mapValues { (_, categoryHabits) ->
                        val completed = categoryHabits.count { it.isCompleted }
                        val total = categoryHabits.size
                        if (total > 0) (completed * 100f) / total else 0f
                    }
                val bestCategory = categoryCompletions.maxByOrNull { it.value }
                val bestCategoryName = if ((bestCategory?.value ?: 0f) > 0f) {
                    bestCategory?.key ?: "-"
                } else "-"

                val weeklyPercentage = if (totalHabits > 0) {
                    (totalCompletedToday * 100f) / totalHabits
                } else 0f

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userInfo = UserInfo(
                            name = "Ahmad Fauzi",
                            email = "ahmad.fauzi@example.com",
                            avatarInitial = "AF",
                            isLoggedIn = true
                        ),
                        totalHabits = totalHabits,
                        totalCompleted = totalCompletedToday,
                        currentStreak = 0,
                        achievements = generateAchievements(
                            totalCompleted = totalCompletedToday,
                            currentStreak = 0,
                            allCompleteToday = allCompleteToday
                        ),
                        weeklySummary = WeeklySummary(
                            completionPercentage = weeklyPercentage,
                            activeDays = if (totalCompletedToday > 0) 1 else 0,
                            totalDays = 7,
                            bestCategory = bestCategoryName
                        )
                    )
                }
            }
        }
    }

    private fun generateAchievements(
        totalCompleted: Int,
        currentStreak: Int,
        allCompleteToday: Boolean
    ): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_step",
                emoji = "\uD83C\uDF1F",
                name = "Langkah Pertama",
                description = "Selesaikan ibadah pertama",
                isUnlocked = totalCompleted >= 1,
                progress = if (totalCompleted >= 1) 1f else 0f
            ),
            Achievement(
                id = "burning",
                emoji = "\uD83D\uDD25",
                name = "Semangat Membara",
                description = "Streak 7 hari berturut",
                isUnlocked = currentStreak >= 7,
                progress = (currentStreak / 7f).coerceAtMost(1f)
            ),
            Achievement(
                id = "consistent",
                emoji = "\u2B50",
                name = "Bintang Konsisten",
                description = "Streak 14 hari berturut",
                isUnlocked = currentStreak >= 14,
                progress = (currentStreak / 14f).coerceAtMost(1f)
            ),
            Achievement(
                id = "champion",
                emoji = "\uD83C\uDFC6",
                name = "Juara Istiqomah",
                description = "Streak 30 hari berturut",
                isUnlocked = currentStreak >= 30,
                progress = (currentStreak / 30f).coerceAtMost(1f)
            ),
            Achievement(
                id = "hundred",
                emoji = "\uD83D\uDCAF",
                name = "Seratus Ibadah",
                description = "100 ibadah total selesai",
                isUnlocked = totalCompleted >= 100,
                progress = (totalCompleted / 100f).coerceAtMost(1f)
            ),
            Achievement(
                id = "sharpshooter",
                emoji = "\uD83C\uDFAF",
                name = "Penembak Jitu",
                description = "Semua habit selesai 1 hari",
                isUnlocked = allCompleteToday,
                progress = if (allCompleteToday) 1f else 0f
            )
        )
    }

    fun showLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = true) }
    }

    fun hideLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = false) }
    }

    fun logout(): Boolean {
        _uiState.update {
            it.copy(
                showLogoutConfirmation = false,
                userInfo = UserInfo(isLoggedIn = false)
            )
        }
        return true
    }
}
