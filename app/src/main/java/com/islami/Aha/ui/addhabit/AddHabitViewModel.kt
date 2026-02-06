package com.islami.Aha.ui.addhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Enum untuk kategori kebiasaan ibadah.
 */
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

/**
 * Enum untuk frekuensi kebiasaan.
 */
enum class HabitFrequency(val displayName: String) {
    DAILY("Setiap Hari"),
    WEEKLY("Setiap Minggu"),
    MONTHLY("Setiap Bulan"),
    CUSTOM("Kustom")
}

/**
 * Data class untuk hari dalam seminggu.
 */
data class DayOfWeek(
    val id: Int,
    val shortName: String,
    val fullName: String,
    val isSelected: Boolean = false
)

/**
 * UI State untuk Add Habit Screen.
 *
 * @property name Nama kebiasaan
 * @property description Deskripsi kebiasaan
 * @property selectedCategory Kategori yang dipilih
 * @property selectedFrequency Frekuensi yang dipilih
 * @property targetCount Target harian/mingguan
 * @property reminderHour Jam pengingat
 * @property reminderMinute Menit pengingat
 * @property isReminderEnabled Apakah pengingat aktif
 * @property selectedDays Hari yang dipilih (untuk frekuensi mingguan/kustom)
 * @property isLoading Menandakan proses penyimpanan sedang berjalan
 * @property nameError Pesan error untuk field nama
 * @property saveSuccess Menandakan penyimpanan berhasil
 * @property isCategoryExpanded Menandakan dropdown kategori sedang terbuka
 * @property isFrequencyExpanded Menandakan dropdown frekuensi sedang terbuka
 * @property showTimePicker Menandakan time picker sedang ditampilkan
 */
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

/**
 * Mendapatkan daftar hari default.
 */
fun getDefaultDays(): List<DayOfWeek> = listOf(
    DayOfWeek(1, "Sen", "Senin", true),
    DayOfWeek(2, "Sel", "Selasa", true),
    DayOfWeek(3, "Rab", "Rabu", true),
    DayOfWeek(4, "Kam", "Kamis", true),
    DayOfWeek(5, "Jum", "Jumat", true),
    DayOfWeek(6, "Sab", "Sabtu", true),
    DayOfWeek(7, "Min", "Minggu", true)
)

/**
 * ViewModel untuk Add Habit Screen.
 *
 * Mengelola:
 * - Form state untuk menambah kebiasaan baru
 * - Validasi input
 * - Proses penyimpanan (lokal)
 *
 * Catatan: Menggunakan penyimpanan lokal dummy untuk demo.
 * Di production, akan menyimpan ke database Room.
 */
class AddHabitViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    // ========================================================================
    // INPUT HANDLERS
    // ========================================================================

    /**
     * Update nama kebiasaan.
     */
    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    /**
     * Update deskripsi kebiasaan.
     */
    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    /**
     * Update kategori yang dipilih.
     */
    fun onCategorySelected(category: HabitCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isCategoryExpanded = false
            )
        }
    }

    /**
     * Toggle dropdown kategori.
     */
    fun toggleCategoryDropdown() {
        _uiState.update { it.copy(isCategoryExpanded = !it.isCategoryExpanded) }
    }

    /**
     * Dismiss dropdown kategori.
     */
    fun dismissCategoryDropdown() {
        _uiState.update { it.copy(isCategoryExpanded = false) }
    }

    /**
     * Update frekuensi yang dipilih.
     */
    fun onFrequencySelected(frequency: HabitFrequency) {
        _uiState.update {
            it.copy(
                selectedFrequency = frequency,
                isFrequencyExpanded = false
            )
        }
    }

    /**
     * Toggle dropdown frekuensi.
     */
    fun toggleFrequencyDropdown() {
        _uiState.update { it.copy(isFrequencyExpanded = !it.isFrequencyExpanded) }
    }

    /**
     * Dismiss dropdown frekuensi.
     */
    fun dismissFrequencyDropdown() {
        _uiState.update { it.copy(isFrequencyExpanded = false) }
    }

    /**
     * Update target count.
     */
    fun onTargetCountChange(count: Int) {
        if (count in 1..99) {
            _uiState.update { it.copy(targetCount = count) }
        }
    }

    /**
     * Increment target count.
     */
    fun incrementTargetCount() {
        val currentCount = _uiState.value.targetCount
        if (currentCount < 99) {
            _uiState.update { it.copy(targetCount = currentCount + 1) }
        }
    }

    /**
     * Decrement target count.
     */
    fun decrementTargetCount() {
        val currentCount = _uiState.value.targetCount
        if (currentCount > 1) {
            _uiState.update { it.copy(targetCount = currentCount - 1) }
        }
    }

    /**
     * Toggle pengingat aktif/nonaktif.
     */
    fun toggleReminder() {
        _uiState.update { it.copy(isReminderEnabled = !it.isReminderEnabled) }
    }

    /**
     * Update waktu pengingat.
     */
    fun onReminderTimeChange(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(
                reminderHour = hour,
                reminderMinute = minute,
                showTimePicker = false
            )
        }
    }

    /**
     * Tampilkan time picker.
     */
    fun showTimePicker() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    /**
     * Sembunyikan time picker.
     */
    fun hideTimePicker() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    /**
     * Toggle hari yang dipilih.
     */
    fun toggleDaySelection(dayId: Int) {
        _uiState.update { currentState ->
            val updatedDays = currentState.selectedDays.map { day ->
                if (day.id == dayId) {
                    day.copy(isSelected = !day.isSelected)
                } else {
                    day
                }
            }
            currentState.copy(selectedDays = updatedDays)
        }
    }

    // ========================================================================
    // SAVE FUNCTION
    // ========================================================================

    /**
     * Menyimpan kebiasaan baru.
     * Validasi input dan simulasi proses penyimpanan.
     */
    fun saveHabit() {
        val currentState = _uiState.value

        // Validasi nama
        val nameError = validateName(currentState.name)
        if (nameError != null) {
            _uiState.update { it.copy(nameError = nameError) }
            return
        }

        // Proses penyimpanan (simulasi)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simulasi delay penyimpanan
            kotlinx.coroutines.delay(800)

            // Untuk demo, penyimpanan selalu berhasil
            // Di production: simpan ke Room database
            _uiState.update {
                it.copy(
                    isLoading = false,
                    saveSuccess = true
                )
            }
        }
    }

    /**
     * Reset state setelah navigasi.
     */
    fun resetState() {
        _uiState.value = AddHabitUiState()
    }

    // ========================================================================
    // VALIDATION
    // ========================================================================

    /**
     * Validasi nama kebiasaan.
     * @return Pesan error atau null jika valid
     */
    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Nama kebiasaan tidak boleh kosong"
            name.length < 3 -> "Nama minimal 3 karakter"
            name.length > 50 -> "Nama maksimal 50 karakter"
            else -> null
        }
    }

    // ========================================================================
    // UTILITY FUNCTIONS
    // ========================================================================

    /**
     * Format waktu pengingat untuk ditampilkan.
     */
    fun getFormattedReminderTime(): String {
        val hour = _uiState.value.reminderHour
        val minute = _uiState.value.reminderMinute
        return String.format("%02d:%02d", hour, minute)
    }

    /**
     * Mendapatkan deskripsi frekuensi yang dipilih.
     */
    fun getFrequencyDescription(): String {
        val state = _uiState.value
        return when (state.selectedFrequency) {
            HabitFrequency.DAILY -> "Setiap hari"
            HabitFrequency.WEEKLY -> {
                val selectedDays = state.selectedDays.filter { it.isSelected }
                if (selectedDays.size == 7) {
                    "Setiap hari"
                } else {
                    selectedDays.joinToString(", ") { it.shortName }
                }
            }
            HabitFrequency.MONTHLY -> "Setiap bulan"
            HabitFrequency.CUSTOM -> {
                val selectedDays = state.selectedDays.filter { it.isSelected }
                selectedDays.joinToString(", ") { it.shortName }
            }
        }
    }
}
