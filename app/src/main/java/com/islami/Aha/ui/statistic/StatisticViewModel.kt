package com.islami.Aha.ui.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islami.Aha.data.local.HabitDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DailyStatistic(
    val dayName: String,
    val completedCount: Int,
    val isToday: Boolean = false
)

data class CategoryStatistic(
    val name: String,
    val icon: String,
    val completedCount: Int,
    val totalCount: Int,
    val percentage: Int,
    val streak: Int,
    val gradientIndex: Int = 0
)

data class StatisticUiState(
    val isLoading: Boolean = true,
    val currentDate: String = "",
    val todayCompleted: Int = 0,
    val todayTotal: Int = 0,
    val todayPercentage: Int = 0,
    val weeklyStats: List<DailyStatistic> = emptyList(),
    val categoryStats: List<CategoryStatistic> = emptyList(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompleted: Int = 0,
    val averagePerDay: Float = 0f
)

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val habitDao: HabitDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticUiState())
    val uiState: StateFlow<StatisticUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            habitDao.getHabits().collect { habits ->
                val todayCompleted = habits.count { it.isCompleted }
                val todayTotal = habits.size
                val todayPercentage = if (todayTotal > 0) (todayCompleted * 100) / todayTotal else 0

                val categoryOrder = listOf("Sholat Fardhu", "Sholat Sunnah", "Puasa Wajib", "Puasa Sunnah")
                val categoryIcons = mapOf(
                    "Sholat Fardhu" to "\uD83D\uDD4C",
                    "Sholat Sunnah" to "\u2600\uFE0F",
                    "Puasa Wajib" to "\uD83C\uDF7D\uFE0F",
                    "Puasa Sunnah" to "\uD83C\uDF19"
                )

                val categoryStats = categoryOrder.mapIndexed { index, categoryName ->
                    val categoryHabits = habits.filter { it.category == categoryName }
                    val completed = categoryHabits.count { it.isCompleted }
                    val total = categoryHabits.size
                    CategoryStatistic(
                        name = categoryName,
                        icon = categoryIcons[categoryName] ?: "\uD83D\uDCCA",
                        completedCount = completed,
                        totalCount = total,
                        percentage = if (total > 0) (completed * 100) / total else 0,
                        streak = 0,
                        gradientIndex = index
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentDate = getCurrentDateFormatted(),
                        todayCompleted = todayCompleted,
                        todayTotal = todayTotal,
                        todayPercentage = todayPercentage,
                        categoryStats = categoryStats,
                        weeklyStats = generateWeeklyStats(todayCompleted),
                        currentStreak = 0,
                        longestStreak = 0,
                        totalCompleted = todayCompleted,
                        averagePerDay = if (todayTotal > 0) todayCompleted.toFloat() else 0f
                    )
                }
            }
        }
    }

    private fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return dateFormat.format(Date())
    }

    private fun generateWeeklyStats(todayCompleted: Int): List<DailyStatistic> {
        val calendar = Calendar.getInstance()
        val todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

        return dayNames.mapIndexed { index, name ->
            val dayOfWeek = index + 1 // Calendar.SUNDAY=1, MONDAY=2, etc.
            DailyStatistic(
                dayName = name,
                completedCount = if (dayOfWeek == todayDayOfWeek) todayCompleted else 0,
                isToday = dayOfWeek == todayDayOfWeek
            )
        }
    }
}
