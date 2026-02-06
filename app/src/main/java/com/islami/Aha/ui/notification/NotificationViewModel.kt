package com.islami.Aha.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class untuk item pengingat/notifikasi.
 *
 * @property id ID unik pengingat
 * @property habitName Nama kebiasaan yang diingatkan
 * @property habitIcon Icon kebiasaan
 * @property time Waktu pengingat dalam format HH:mm
 * @property days Hari-hari pengingat aktif
 * @property isEnabled Apakah pengingat aktif
 * @property category Kategori kebiasaan
 */
data class ReminderItem(
    val id: String,
    val habitName: String,
    val habitIcon: String,
    val time: String,
    val days: String,
    val isEnabled: Boolean,
    val category: String
)

/**
 * UI State untuk Notification Screen.
 *
 * @property isLoading Menandakan data sedang dimuat
 * @property reminders Daftar pengingat
 * @property globalNotificationEnabled Apakah notifikasi global aktif
 * @property showDeleteConfirmation Menampilkan dialog konfirmasi hapus
 * @property reminderToDelete Pengingat yang akan dihapus
 */
data class NotificationUiState(
    val isLoading: Boolean = true,
    val reminders: List<ReminderItem> = emptyList(),
    val globalNotificationEnabled: Boolean = true,
    val showDeleteConfirmation: Boolean = false,
    val reminderToDelete: ReminderItem? = null
)

/**
 * ViewModel untuk Notification Screen.
 *
 * Mengelola:
 * - Daftar pengingat ibadah
 * - Toggle aktif/nonaktif pengingat
 * - Hapus pengingat
 *
 * Catatan: Menggunakan data dummy untuk demo.
 * Di production, akan membaca dari database Room dan WorkManager.
 */
class NotificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    /**
     * Memuat daftar pengingat.
     */
    private fun loadReminders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simulasi loading
            kotlinx.coroutines.delay(500)

            val reminders = generateDummyReminders()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    reminders = reminders
                )
            }
        }
    }

    /**
     * Refresh daftar pengingat.
     */
    fun refreshReminders() {
        loadReminders()
    }

    /**
     * Toggle notifikasi global.
     */
    fun toggleGlobalNotification() {
        _uiState.update {
            it.copy(globalNotificationEnabled = !it.globalNotificationEnabled)
        }
    }

    /**
     * Toggle status aktif pengingat individual.
     */
    fun toggleReminderEnabled(reminderId: String) {
        _uiState.update { currentState ->
            val updatedReminders = currentState.reminders.map { reminder ->
                if (reminder.id == reminderId) {
                    reminder.copy(isEnabled = !reminder.isEnabled)
                } else {
                    reminder
                }
            }
            currentState.copy(reminders = updatedReminders)
        }
    }

    /**
     * Menampilkan dialog konfirmasi hapus.
     */
    fun showDeleteConfirmation(reminder: ReminderItem) {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = true,
                reminderToDelete = reminder
            )
        }
    }

    /**
     * Menyembunyikan dialog konfirmasi hapus.
     */
    fun hideDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                reminderToDelete = null
            )
        }
    }

    /**
     * Menghapus pengingat.
     */
    fun deleteReminder() {
        val reminderToDelete = _uiState.value.reminderToDelete ?: return

        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedReminders = currentState.reminders.filter {
                    it.id != reminderToDelete.id
                }
                currentState.copy(
                    reminders = updatedReminders,
                    showDeleteConfirmation = false,
                    reminderToDelete = null
                )
            }
        }
    }

    /**
     * Generate data dummy untuk pengingat.
     */
    private fun generateDummyReminders(): List<ReminderItem> {
        return listOf(
            ReminderItem(
                id = "1",
                habitName = "Sholat Subuh",
                habitIcon = "🕌",
                time = "04:30",
                days = "Setiap hari",
                isEnabled = true,
                category = "Sholat Fardhu"
            ),
            ReminderItem(
                id = "2",
                habitName = "Sholat Dzuhur",
                habitIcon = "🕌",
                time = "12:00",
                days = "Setiap hari",
                isEnabled = true,
                category = "Sholat Fardhu"
            ),
            ReminderItem(
                id = "3",
                habitName = "Sholat Ashar",
                habitIcon = "🕌",
                time = "15:15",
                days = "Setiap hari",
                isEnabled = true,
                category = "Sholat Fardhu"
            ),
            ReminderItem(
                id = "4",
                habitName = "Sholat Maghrib",
                habitIcon = "🕌",
                time = "18:00",
                days = "Setiap hari",
                isEnabled = true,
                category = "Sholat Fardhu"
            ),
            ReminderItem(
                id = "5",
                habitName = "Sholat Isya",
                habitIcon = "🕌",
                time = "19:15",
                days = "Setiap hari",
                isEnabled = true,
                category = "Sholat Fardhu"
            ),
            ReminderItem(
                id = "6",
                habitName = "Sholat Dhuha",
                habitIcon = "☀️",
                time = "08:00",
                days = "Sen, Sel, Rab, Kam, Jum",
                isEnabled = true,
                category = "Sholat Sunnah"
            ),
            ReminderItem(
                id = "7",
                habitName = "Sholat Tahajud",
                habitIcon = "🌃",
                time = "03:00",
                days = "Sen, Rab, Jum",
                isEnabled = false,
                category = "Sholat Sunnah"
            ),
            ReminderItem(
                id = "8",
                habitName = "Dzikir Pagi",
                habitIcon = "📿",
                time = "05:30",
                days = "Setiap hari",
                isEnabled = true,
                category = "Dzikir"
            ),
            ReminderItem(
                id = "9",
                habitName = "Dzikir Petang",
                habitIcon = "📿",
                time = "17:00",
                days = "Setiap hari",
                isEnabled = true,
                category = "Dzikir"
            ),
            ReminderItem(
                id = "10",
                habitName = "Tilawah Al-Quran",
                habitIcon = "📖",
                time = "20:00",
                days = "Setiap hari",
                isEnabled = true,
                category = "Tilawah"
            )
        )
    }
}
