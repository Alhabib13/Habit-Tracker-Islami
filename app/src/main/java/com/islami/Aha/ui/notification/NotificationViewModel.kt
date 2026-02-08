package com.islami.Aha.ui.notification

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islami.Aha.data.local.HabitDao
import com.islami.Aha.data.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = true,
    val habits: List<Habit> = emptyList(),
    val globalNotificationEnabled: Boolean = true,
    val showDeleteConfirmation: Boolean = false,
    val habitToDelete: Habit? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val habitDao: HabitDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        seedDataIfNeeded()
        loadHabitsAsReminders()
    }

    private fun seedDataIfNeeded() {
        viewModelScope.launch {
            val hasSeeded = sharedPreferences.getBoolean("hasSeeded", false)
            if (!hasSeeded) {
                val count = habitDao.getHabitCount()
                if (count == 0) {
                    habitDao.insertAll(getDefaultHabits())
                    sharedPreferences.edit().putBoolean("hasSeeded", true).apply()
                }
            }
        }
    }

    private fun getDefaultHabits(): List<Habit> = listOf(
        Habit(name = "Sholat Subuh", category = "Sholat Fardhu", icon = "\uD83C\uDF05", description = "", time = "04:30"),
        Habit(name = "Sholat Dzuhur", category = "Sholat Fardhu", icon = "\u2600\uFE0F", description = "", time = "11:55"),
        Habit(name = "Sholat Ashar", category = "Sholat Fardhu", icon = "\u2601\uFE0F", description = "", time = "15:10"),
        Habit(name = "Sholat Maghrib", category = "Sholat Fardhu", icon = "\uD83C\uDF19", description = "", time = "18:00"),
        Habit(name = "Sholat Isya", category = "Sholat Fardhu", icon = "\uD83C\uDF1C", description = "", time = "19:15"),
        Habit(name = "Sholat Dhuha", category = "Sholat Sunnah", icon = "\u2600\uFE0F", description = "06:00 - 11:00", time = "06:00"),
        Habit(name = "Qabliyah Dzuhur", category = "Sholat Sunnah", icon = "\u2600\uFE0F", description = "", time = "11:30"),
        Habit(name = "Ba'diyah Dzuhur", category = "Sholat Sunnah", icon = "\u2600\uFE0F", description = "", time = "12:15"),
        Habit(name = "Ba'diyah Maghrib", category = "Sholat Sunnah", icon = "\uD83C\uDF19", description = "", time = "18:20"),
        Habit(name = "Ba'diyah Isya", category = "Sholat Sunnah", icon = "\uD83C\uDF1C", description = "", time = "19:35"),
        Habit(name = "Tahajud", category = "Sholat Sunnah", icon = "\uD83C\uDF19", description = "", time = "03:00"),
        Habit(name = "Witir", category = "Sholat Sunnah", icon = "\uD83C\uDF19", description = "", time = "03:30"),
        Habit(name = "Puasa Ramadan", category = "Puasa Wajib", icon = "\uD83C\uDF7D\uFE0F", description = "Sahur - Maghrib", time = ""),
        Habit(name = "Puasa Senin", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "Setiap Senin", time = ""),
        Habit(name = "Puasa Kamis", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "Setiap Kamis", time = ""),
        Habit(name = "Puasa Ayyamul Bidh", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "13-15 Hijriah", time = ""),
        Habit(name = "Puasa Daud", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "Selang-seling", time = ""),
        Habit(name = "Puasa Syawal", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "6 hari di bulan Syawal", time = "")
    )

    private fun loadHabitsAsReminders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            habitDao.getHabits().collect { habits ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        habits = habits
                    )
                }
            }
        }
    }

    fun toggleGlobalNotification() {
        _uiState.update { it.copy(globalNotificationEnabled = !it.globalNotificationEnabled) }
    }

    fun toggleReminderEnabled(habit: Habit) {
        viewModelScope.launch {
            val updatedHabit = habit.copy(isReminderEnabled = !habit.isReminderEnabled)
            habitDao.updateHabit(updatedHabit)
        }
    }

    fun showDeleteConfirmation(habit: Habit) {
        _uiState.update { it.copy(showDeleteConfirmation = true, habitToDelete = habit) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false, habitToDelete = null) }
    }

    fun deleteReminder() {
        val habitToDelete = _uiState.value.habitToDelete ?: return

        viewModelScope.launch {
            habitDao.deleteHabit(habitToDelete)
            hideDeleteConfirmation()
        }
    }
}
