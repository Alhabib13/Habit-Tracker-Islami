package com.islami.Aha.ui.addhabit

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

enum class HabitCategory(val displayName: String, val icon: String) {
    SHOLAT_FARDHU("Sholat Fardhu", "🕌"),
    SHOLAT_SUNNAH("Sholat Sunnah", "🤲"),
    DZIKIR("Dzikir", "📿"),
    TILAWAH("Tilawah Al-Quran", "📖"),
    PUASA("Puasa Sunnah", "🌙"),
    SEDEKAH("Sedekah", "💝"),
    DHUHA("Sholat Dhuha", "☀️"),
    TAHAJUD("Sholat Tahajud", "🌃"),
    LAINNYA("Lainnya", "✨")
}

enum class HabitFrequency(val displayName: String) {
    DAILY("Setiap Hari"),
    WEEKLY("Setiap Minggu"),
    MONTHLY("Setiap Bulan"),
    CUSTOM("Kustom")
}

data class DayOfWeek(
    val id: Int,
    val shortName: String,
    val fullName: String,
    val isSelected: Boolean = false
)

data class AddHabitUiState(
    val name: String = "",
    val description: String = "",
    val selectedCategory: HabitCategory = HabitCategory.SHOLAT_FARDHU,
    val selectedFrequency: HabitFrequency = HabitFrequency.DAILY,
    val targetCount: Int = 1,
    val reminderHour: Int = 5,
    val reminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true,
    val selectedDays: List<DayOfWeek> = getDefaultDays(),
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val saveSuccess: Boolean = false,
    val isCategoryExpanded: Boolean = false,
    val isFrequencyExpanded: Boolean = false,
    val showTimePicker: Boolean = false
)

fun getDefaultDays(): List<DayOfWeek> = listOf(
    DayOfWeek(1, "Sen", "Senin", true),
    DayOfWeek(2, "Sel", "Selasa", true),
    DayOfWeek(3, "Rab", "Rabu", true),
    DayOfWeek(4, "Kam", "Kamis", true),
    DayOfWeek(5, "Jum", "Jumat", true),
    DayOfWeek(6, "Sab", "Sabtu", true),
    DayOfWeek(7, "Min", "Minggu", true)
)

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val habitDao: HabitDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onCategorySelected(category: HabitCategory) {
        _uiState.update { it.copy(selectedCategory = category, isCategoryExpanded = false) }
    }

    fun toggleCategoryDropdown() {
        _uiState.update { it.copy(isCategoryExpanded = !it.isCategoryExpanded) }
    }

    fun dismissCategoryDropdown() {
        _uiState.update { it.copy(isCategoryExpanded = false) }
    }

    fun onFrequencySelected(frequency: HabitFrequency) {
        _uiState.update { it.copy(selectedFrequency = frequency, isFrequencyExpanded = false) }
    }

    fun toggleFrequencyDropdown() {
        _uiState.update { it.copy(isFrequencyExpanded = !it.isFrequencyExpanded) }
    }

    fun dismissFrequencyDropdown() {
        _uiState.update { it.copy(isFrequencyExpanded = false) }
    }

    fun incrementTargetCount() {
        val currentCount = _uiState.value.targetCount
        if (currentCount < 99) {
            _uiState.update { it.copy(targetCount = currentCount + 1) }
        }
    }

    fun decrementTargetCount() {
        val currentCount = _uiState.value.targetCount
        if (currentCount > 1) {
            _uiState.update { it.copy(targetCount = currentCount - 1) }
        }
    }

    fun toggleReminder() {
        _uiState.update { it.copy(isReminderEnabled = !it.isReminderEnabled) }
    }

    fun onReminderTimeChange(hour: Int, minute: Int) {
        _uiState.update { it.copy(reminderHour = hour, reminderMinute = minute, showTimePicker = false) }
    }

    fun showTimePicker() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    fun hideTimePicker() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    fun toggleDaySelection(dayId: Int) {
        _uiState.update { currentState ->
            val updatedDays = currentState.selectedDays.map { day ->
                if (day.id == dayId) day.copy(isSelected = !day.isSelected) else day
            }
            currentState.copy(selectedDays = updatedDays)
        }
    }

    fun saveHabit() {
        val currentState = _uiState.value
        val nameError = validateName(currentState.name)
        if (nameError != null) {
            _uiState.update { it.copy(nameError = nameError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val newHabit = Habit(
                name = currentState.name,
                description = currentState.description,
                category = currentState.selectedCategory.displayName,
                icon = currentState.selectedCategory.icon
            )

            habitDao.insertHabit(newHabit)

            _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
        }
    }

    fun resetState() {
        _uiState.value = AddHabitUiState()
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Nama kebiasaan tidak boleh kosong"
            name.length < 3 -> "Nama minimal 3 karakter"
            name.length > 50 -> "Nama maksimal 50 karakter"
            else -> null
        }
    }

    fun getFormattedReminderTime(): String {
        val hour = _uiState.value.reminderHour
        val minute = _uiState.value.reminderMinute
        return String.format("%02d:%02d", hour, minute)
    }

    fun getFrequencyDescription(): String {
        val state = _uiState.value
        return when (state.selectedFrequency) {
            HabitFrequency.DAILY -> "Setiap hari"
            HabitFrequency.WEEKLY -> {
                val selectedDays = state.selectedDays.filter { it.isSelected }
                if (selectedDays.size == 7) "Setiap hari" else selectedDays.joinToString(", ") { it.shortName }
            }
            HabitFrequency.MONTHLY -> "Setiap bulan"
            HabitFrequency.CUSTOM -> {
                val selectedDays = state.selectedDays.filter { it.isSelected }
                selectedDays.joinToString(", ") { it.shortName }
            }
        }
    }
}
