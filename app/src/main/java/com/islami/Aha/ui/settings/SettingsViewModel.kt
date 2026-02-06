package com.islami.Aha.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Enum untuk format waktu yang tersedia.
 * Digunakan untuk menampilkan waktu dalam format 12 jam atau 24 jam.
 */
enum class TimeFormatOption(val displayName: String, val description: String) {
    HOUR_24("24 Jam", "Contoh: 14:30"),
    HOUR_12("12 Jam", "Contoh: 2:30 PM")
}

/**
 * UI State untuk Settings Screen.
 *
 * Data class ini menyimpan semua state yang diperlukan untuk
 * menampilkan dan mengelola layar pengaturan.
 *
 * @property notificationEnabled Status notifikasi aktif/nonaktif
 * @property darkModeEnabled Status mode gelap aktif/nonaktif
 * @property selectedTimeFormat Format waktu yang dipilih (12/24 jam)
 * @property showTimeFormatDialog Menampilkan dialog pilih format waktu
 *
 * Catatan untuk Tesis:
 * State ini hanya disimpan di memori (volatile) dan akan reset
 * ketika aplikasi ditutup. Untuk implementasi production, gunakan
 * DataStore atau SharedPreferences untuk persistensi.
 */
data class SettingsUiState(
    val notificationEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val selectedTimeFormat: TimeFormatOption = TimeFormatOption.HOUR_24,
    val showTimeFormatDialog: Boolean = false
)

/**
 * ViewModel untuk Settings Screen.
 *
 * ViewModel ini mengelola state pengaturan aplikasi dengan pola MVVM.
 * Menggunakan StateFlow untuk reactive state management yang sesuai
 * dengan Jetpack Compose.
 *
 * Fitur yang dikelola:
 * 1. Toggle Notifikasi - Mengaktifkan/menonaktifkan pengingat
 * 2. Toggle Mode Gelap - Mengubah tema aplikasi (UI only)
 * 3. Format Waktu - Memilih format 12 jam atau 24 jam
 *
 * Catatan Implementasi:
 * - State disimpan di memori (tidak persisten)
 * - Untuk production, integrasi dengan DataStore diperlukan
 * - Dark mode hanya mengubah state UI, tidak mengubah tema aplikasi
 *
 * @see SettingsUiState untuk definisi state
 * @see SettingsScreen untuk implementasi UI
 */
class SettingsViewModel : ViewModel() {

    // ========================================================================
    // STATE MANAGEMENT
    // ========================================================================

    /**
     * Private mutable state yang hanya bisa diubah dari dalam ViewModel.
     */
    private val _uiState = MutableStateFlow(SettingsUiState())

    /**
     * Public read-only state yang dapat diobservasi oleh UI.
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // ========================================================================
    // NOTIFICATION SETTINGS
    // ========================================================================

    /**
     * Toggle status notifikasi.
     *
     * Mengubah state notifikasi antara aktif dan nonaktif.
     * Di production, perubahan ini harus:
     * 1. Disimpan ke DataStore/SharedPreferences
     * 2. Mendaftarkan/membatalkan WorkManager untuk alarm
     */
    fun toggleNotification() {
        _uiState.update { currentState ->
            currentState.copy(notificationEnabled = !currentState.notificationEnabled)
        }
    }

    /**
     * Set status notifikasi secara eksplisit.
     *
     * @param enabled true untuk mengaktifkan, false untuk menonaktifkan
     */
    fun setNotificationEnabled(enabled: Boolean) {
        _uiState.update { it.copy(notificationEnabled = enabled) }
    }

    // ========================================================================
    // DARK MODE SETTINGS
    // ========================================================================

    /**
     * Toggle status mode gelap.
     *
     * Mengubah state mode gelap antara aktif dan nonaktif.
     * Catatan: Saat ini hanya mengubah state UI.
     * Untuk implementasi penuh, perlu:
     * 1. Menyimpan preferensi ke DataStore
     * 2. Mengubah parameter darkTheme di HabitIslamiTheme
     */
    fun toggleDarkMode() {
        _uiState.update { currentState ->
            currentState.copy(darkModeEnabled = !currentState.darkModeEnabled)
        }
    }

    /**
     * Set status mode gelap secara eksplisit.
     *
     * @param enabled true untuk mengaktifkan, false untuk menonaktifkan
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    // ========================================================================
    // TIME FORMAT SETTINGS
    // ========================================================================

    /**
     * Menampilkan dialog pemilihan format waktu.
     */
    fun showTimeFormatDialog() {
        _uiState.update { it.copy(showTimeFormatDialog = true) }
    }

    /**
     * Menyembunyikan dialog pemilihan format waktu.
     */
    fun hideTimeFormatDialog() {
        _uiState.update { it.copy(showTimeFormatDialog = false) }
    }

    /**
     * Mengubah format waktu yang dipilih.
     *
     * @param format Format waktu baru yang dipilih
     */
    fun setTimeFormat(format: TimeFormatOption) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedTimeFormat = format,
                showTimeFormatDialog = false
            )
        }
    }

    // ========================================================================
    // UTILITY FUNCTIONS
    // ========================================================================

    /**
     * Reset semua pengaturan ke nilai default.
     *
     * Mengembalikan semua pengaturan ke state awal:
     * - Notifikasi: Aktif
     * - Mode Gelap: Nonaktif
     * - Format Waktu: 24 Jam
     */
    fun resetToDefaults() {
        _uiState.value = SettingsUiState()
    }

    /**
     * Mendapatkan ringkasan pengaturan saat ini.
     *
     * @return String yang mendeskripsikan pengaturan aktif
     */
    fun getSettingsSummary(): String {
        val state = _uiState.value
        return buildString {
            append("Notifikasi: ${if (state.notificationEnabled) "Aktif" else "Nonaktif"}")
            append("\n")
            append("Mode Gelap: ${if (state.darkModeEnabled) "Aktif" else "Nonaktif"}")
            append("\n")
            append("Format Waktu: ${state.selectedTimeFormat.displayName}")
        }
    }
}
