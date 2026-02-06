package com.islami.Aha.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State untuk Splash Screen.
 *
 * @property isLoading Menandakan apakah splash screen masih loading
 * @property isUserLoggedIn Menandakan apakah user sudah login sebelumnya
 * @property shouldNavigate Trigger untuk memulai navigasi
 */
data class SplashUiState(
    val isLoading: Boolean = true,
    val isUserLoggedIn: Boolean = false,
    val shouldNavigate: Boolean = false
)

/**
 * ViewModel untuk Splash Screen.
 *
 * Bertanggung jawab untuk:
 * 1. Menampilkan splash selama 2 detik
 * 2. Mengecek status login user (dari local storage)
 * 3. Menentukan navigasi berikutnya (Login atau Home)
 *
 * Untuk saat ini, logic login menggunakan dummy data.
 * Di production, akan membaca dari SharedPreferences atau DataStore.
 */
class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    // Durasi splash screen dalam milliseconds
    companion object {
        private const val SPLASH_DURATION = 2000L
    }

    init {
        startSplashTimer()
    }

    /**
     * Memulai timer splash screen.
     * Setelah durasi selesai, akan mengecek status login dan trigger navigasi.
     */
    private fun startSplashTimer() {
        viewModelScope.launch {
            // Tampilkan splash selama durasi yang ditentukan
            delay(SPLASH_DURATION)

            // Cek status login
            val isLoggedIn = checkUserLoginStatus()

            // Update state untuk trigger navigasi
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    isUserLoggedIn = isLoggedIn,
                    shouldNavigate = true
                )
            }
        }
    }

    /**
     * Mengecek apakah user sudah login sebelumnya.
     *
     * TODO: Implementasi sebenarnya menggunakan SharedPreferences atau DataStore
     * untuk menyimpan dan membaca status login.
     *
     * @return true jika user sudah login, false jika belum
     */
    private suspend fun checkUserLoginStatus(): Boolean {
        // Simulasi pengecekan login (delay kecil untuk realistis)
        delay(100)

        // TODO: Ganti dengan logic sebenarnya
        // Contoh implementasi dengan SharedPreferences:
        // return sharedPreferences.getBoolean("is_logged_in", false)

        // Untuk sementara, selalu return false (selalu ke Login)
        return false
    }

    /**
     * Reset state navigasi.
     * Dipanggil setelah navigasi selesai untuk mencegah navigasi berulang.
     */
    fun onNavigationComplete() {
        _uiState.update { currentState ->
            currentState.copy(shouldNavigate = false)
        }
    }
}
