package com.islami.Aha.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class untuk informasi pengguna.
 *
 * @property name Nama pengguna
 * @property email Email pengguna
 * @property avatarInitial Inisial untuk avatar
 * @property isLoggedIn Apakah pengguna sudah login
 */
data class UserInfo(
    val name: String = "Pengguna Lokal",
    val email: String = "",
    val avatarInitial: String = "P",
    val isLoggedIn: Boolean = false
)

/**
 * Enum untuk format waktu.
 */
enum class TimeFormat(val displayName: String) {
    HOUR_24("24 Jam"),
    HOUR_12("12 Jam (AM/PM)")
}

/**
 * UI State untuk Profile Screen.
 *
 * @property isLoading Menandakan data sedang dimuat
 * @property userInfo Informasi pengguna
 * @property notificationEnabled Apakah notifikasi aktif
 * @property darkModeEnabled Apakah mode gelap aktif
 * @property timeFormat Format waktu yang dipilih
 * @property showResetConfirmation Menampilkan dialog konfirmasi reset
 * @property showLogoutConfirmation Menampilkan dialog konfirmasi logout
 * @property showTimeFormatDialog Menampilkan dialog pilih format waktu
 * @property totalHabits Total kebiasaan
 * @property totalCompleted Total selesai
 * @property currentStreak Streak saat ini
 * @property appVersion Versi aplikasi
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val userInfo: UserInfo = UserInfo(),
    val notificationEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val timeFormat: TimeFormat = TimeFormat.HOUR_24,
    val showResetConfirmation: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val showTimeFormatDialog: Boolean = false,
    val totalHabits: Int = 0,
    val totalCompleted: Int = 0,
    val currentStreak: Int = 0,
    val appVersion: String = "1.0.0"
)

/**
 * ViewModel untuk Profile Screen.
 *
 * Mengelola:
 * - Informasi pengguna
 * - Pengaturan aplikasi (notifikasi, tema, format waktu)
 * - Reset data
 * - Logout
 *
 * Catatan: Menggunakan data dummy untuk demo.
 * Di production, akan membaca dari SharedPreferences/DataStore dan Room.
 */
class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    /**
     * Memuat data profil.
     */
    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simulasi loading
            kotlinx.coroutines.delay(500)

            // Dummy data - di production akan dari DataStore/Room
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userInfo = UserInfo(
                        name = "Ahmad Fauzi",
                        email = "ahmad.fauzi@example.com",
                        avatarInitial = "AF",
                        isLoggedIn = true
                    ),
                    notificationEnabled = true,
                    darkModeEnabled = false,
                    timeFormat = TimeFormat.HOUR_24,
                    totalHabits = 10,
                    totalCompleted = 156,
                    currentStreak = 7,
                    appVersion = "1.0.0"
                )
            }
        }
    }

    /**
     * Refresh data profil.
     */
    fun refreshProfile() {
        loadProfile()
    }

    // ========================================================================
    // SETTINGS HANDLERS
    // ========================================================================

    /**
     * Toggle notifikasi.
     */
    fun toggleNotification() {
        _uiState.update {
            it.copy(notificationEnabled = !it.notificationEnabled)
        }
        // Di production: simpan ke SharedPreferences/DataStore
    }

    /**
     * Toggle mode gelap.
     */
    fun toggleDarkMode() {
        _uiState.update {
            it.copy(darkModeEnabled = !it.darkModeEnabled)
        }
        // Di production: simpan ke SharedPreferences/DataStore dan update theme
    }

    /**
     * Menampilkan dialog pilih format waktu.
     */
    fun showTimeFormatDialog() {
        _uiState.update { it.copy(showTimeFormatDialog = true) }
    }

    /**
     * Menyembunyikan dialog pilih format waktu.
     */
    fun hideTimeFormatDialog() {
        _uiState.update { it.copy(showTimeFormatDialog = false) }
    }

    /**
     * Mengubah format waktu.
     */
    fun setTimeFormat(format: TimeFormat) {
        _uiState.update {
            it.copy(
                timeFormat = format,
                showTimeFormatDialog = false
            )
        }
        // Di production: simpan ke SharedPreferences/DataStore
    }

    // ========================================================================
    // RESET DATA HANDLERS
    // ========================================================================

    /**
     * Menampilkan dialog konfirmasi reset.
     */
    fun showResetConfirmation() {
        _uiState.update { it.copy(showResetConfirmation = true) }
    }

    /**
     * Menyembunyikan dialog konfirmasi reset.
     */
    fun hideResetConfirmation() {
        _uiState.update { it.copy(showResetConfirmation = false) }
    }

    /**
     * Reset semua data.
     */
    fun resetAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showResetConfirmation = false) }

            // Simulasi proses reset
            kotlinx.coroutines.delay(1000)

            // Di production: hapus data dari Room database
            _uiState.update {
                it.copy(
                    isLoading = false,
                    totalHabits = 0,
                    totalCompleted = 0,
                    currentStreak = 0
                )
            }
        }
    }

    // ========================================================================
    // LOGOUT HANDLERS
    // ========================================================================

    /**
     * Menampilkan dialog konfirmasi logout.
     */
    fun showLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = true) }
    }

    /**
     * Menyembunyikan dialog konfirmasi logout.
     */
    fun hideLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = false) }
    }

    /**
     * Logout pengguna.
     * @return true jika berhasil logout
     */
    fun logout(): Boolean {
        // Di production: clear session, tokens, etc.
        _uiState.update {
            it.copy(
                showLogoutConfirmation = false,
                userInfo = UserInfo() // Reset ke default
            )
        }
        return true
    }
}
