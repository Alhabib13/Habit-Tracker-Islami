package com.islami.Aha.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State untuk Login Screen.
 *
 * @property email Input email dari user
 * @property password Input password dari user
 * @property isPasswordVisible Toggle visibility password
 * @property isLoading Menandakan proses login sedang berjalan
 * @property emailError Pesan error untuk field email
 * @property passwordError Pesan error untuk field password
 * @property loginSuccess Menandakan login berhasil
 * @property errorMessage Pesan error umum
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * UI State untuk Register Screen.
 *
 * @property name Input nama lengkap dari user
 * @property email Input email dari user
 * @property password Input password dari user
 * @property confirmPassword Input konfirmasi password
 * @property isPasswordVisible Toggle visibility password
 * @property isConfirmPasswordVisible Toggle visibility konfirmasi password
 * @property isLoading Menandakan proses registrasi sedang berjalan
 * @property nameError Pesan error untuk field nama
 * @property emailError Pesan error untuk field email
 * @property passwordError Pesan error untuk field password
 * @property confirmPasswordError Pesan error untuk field konfirmasi password
 * @property registerSuccess Menandakan registrasi berhasil
 * @property errorMessage Pesan error umum
 */
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk layar Authentication (Login & Register).
 *
 * Mengelola:
 * - State untuk Login dan Register
 * - Validasi input
 * - Proses autentikasi (lokal/dummy)
 *
 * Catatan: Untuk saat ini menggunakan validasi lokal tanpa backend.
 * Di production, akan terhubung ke API atau Firebase Auth.
 */
class AuthViewModel : ViewModel() {

    // ========================================================================
    // LOGIN STATE
    // ========================================================================

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    // ========================================================================
    // REGISTER STATE
    // ========================================================================

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // ========================================================================
    // LOGIN FUNCTIONS
    // ========================================================================

    /**
     * Update email pada login form.
     */
    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email, emailError = null) }
    }

    /**
     * Update password pada login form.
     */
    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(password = password, passwordError = null) }
    }

    /**
     * Toggle visibility password pada login form.
     */
    fun toggleLoginPasswordVisibility() {
        _loginState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    /**
     * Proses login.
     * Validasi input dan simulasi proses login.
     */
    fun login() {
        val currentState = _loginState.value

        // Validasi
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {
            _loginState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        // Proses login (simulasi)
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }

            // Simulasi delay network
            kotlinx.coroutines.delay(1000)

            // Untuk demo, login selalu berhasil
            // Di production: panggil API login
            _loginState.update {
                it.copy(
                    isLoading = false,
                    loginSuccess = true
                )
            }
        }
    }

    /**
     * Reset login state setelah navigasi.
     */
    fun resetLoginState() {
        _loginState.value = LoginUiState()
    }

    // ========================================================================
    // REGISTER FUNCTIONS
    // ========================================================================

    /**
     * Update nama pada register form.
     */
    fun onRegisterNameChange(name: String) {
        _registerState.update { it.copy(name = name, nameError = null) }
    }

    /**
     * Update email pada register form.
     */
    fun onRegisterEmailChange(email: String) {
        _registerState.update { it.copy(email = email, emailError = null) }
    }

    /**
     * Update password pada register form.
     */
    fun onRegisterPasswordChange(password: String) {
        _registerState.update { it.copy(password = password, passwordError = null) }
    }

    /**
     * Update konfirmasi password pada register form.
     */
    fun onRegisterConfirmPasswordChange(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    /**
     * Toggle visibility password pada register form.
     */
    fun toggleRegisterPasswordVisibility() {
        _registerState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    /**
     * Toggle visibility konfirmasi password pada register form.
     */
    fun toggleRegisterConfirmPasswordVisibility() {
        _registerState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    /**
     * Proses registrasi.
     * Validasi input dan simulasi proses registrasi.
     */
    fun register() {
        val currentState = _registerState.value

        // Validasi
        val nameError = validateName(currentState.name)
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = validateConfirmPassword(
            currentState.password,
            currentState.confirmPassword
        )

        if (nameError != null || emailError != null ||
            passwordError != null || confirmPasswordError != null) {
            _registerState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        // Proses registrasi (simulasi)
        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, errorMessage = null) }

            // Simulasi delay network
            kotlinx.coroutines.delay(1500)

            // Untuk demo, registrasi selalu berhasil
            // Di production: panggil API register
            _registerState.update {
                it.copy(
                    isLoading = false,
                    registerSuccess = true
                )
            }
        }
    }

    /**
     * Reset register state setelah navigasi.
     */
    fun resetRegisterState() {
        _registerState.value = RegisterUiState()
    }

    // ========================================================================
    // VALIDATION FUNCTIONS
    // ========================================================================

    /**
     * Validasi nama.
     * @return Pesan error atau null jika valid
     */
    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Nama tidak boleh kosong"
            name.length < 3 -> "Nama minimal 3 karakter"
            else -> null
        }
    }

    /**
     * Validasi email.
     * @return Pesan error atau null jika valid
     */
    private fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return when {
            email.isBlank() -> "Email tidak boleh kosong"
            !email.matches(emailRegex) -> "Format email tidak valid"
            else -> null
        }
    }

    /**
     * Validasi password.
     * @return Pesan error atau null jika valid
     */
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password tidak boleh kosong"
            password.length < 6 -> "Password minimal 6 karakter"
            else -> null
        }
    }

    /**
     * Validasi konfirmasi password.
     * @return Pesan error atau null jika valid
     */
    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Konfirmasi password tidak boleh kosong"
            confirmPassword != password -> "Password tidak cocok"
            else -> null
        }
    }
}
