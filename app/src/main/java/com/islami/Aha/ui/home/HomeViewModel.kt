package com.islami.Aha.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a prayer time with its state.
 * Used for both Fardhu and Sunnah prayers.
 */
data class PrayerTime(
    val name: String,
    val time: String,
    val icon: ImageVector,
    val isCompleted: Boolean = false,
    val notificationEnabled: Boolean = true,
    val timeInMinutes: Int = 0  // Time in minutes from midnight for calculations
)

/**
 * Represents the current prayer status including countdown.
 */
data class PrayerStatus(
    val currentPrayerName: String = "",
    val nextPrayerName: String = "",
    val countdownMinutes: Int = 0,
    val isCurrentPrayerActive: Boolean = false
)

/**
 * Complete UI state for the Home screen.
 * All UI components should derive their state from this single source of truth.
 */
data class HomeUiState(
    // Loading state
    val isLoading: Boolean = true,

    val currentTime: String = "",
    val location: String = "Jakarta",
    val gregorianDate: String = "",
    val hijriDate: String = "",

    // Quick access completion states
    val sholatCompleted: Boolean = false,
    val dzikirCompleted: Boolean = false,
    val tilawahCompleted: Boolean = false,
    val puasaCompleted: Boolean = false,

    // Habit section
    val completedHabitsCount: Int = 0,
    val totalHabitsCount: Int = 5,
    val selectedPrayerTab: String = "fardhu",

    // Prayer status
    val prayerStatus: PrayerStatus = PrayerStatus(),
    val prayerTimes: List<PrayerTime> = emptyList(),

    // Motivation
    val motivationalQuote: String = "",
    val quoteSource: String = ""
)

open class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Prayer times in minutes from midnight (Jakarta timezone)
    private val prayerSchedule = mapOf(
        "Subuh" to (4 * 60 + 30),      // 04:30
        "Dzuhur" to (11 * 60 + 55),    // 11:55
        "Ashar" to (15 * 60 + 10),     // 15:10
        "Maghrib" to (18 * 60),        // 18:00
        "Isya" to (19 * 60 + 15)       // 19:15
    )

    init {
        initializeData()
        startTimeUpdates()
    }

    /**
     * Initialize all data for the Home screen.
     */
    private fun initializeData() {
        viewModelScope.launch {
            // Set loading state
            _uiState.update { it.copy(isLoading = true) }

            val fardhuPrayers = getFardhuPrayerTimes()
            val completedCount = fardhuPrayers.count { it.isCompleted }
            val quote = getRandomMotivationalQuote()

            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    currentTime = getCurrentTime(),
                    gregorianDate = getGregorianDate(),
                    hijriDate = getHijriDate(),
                    location = "Jakarta",
                    prayerTimes = fardhuPrayers,
                    prayerStatus = calculatePrayerStatus(),
                    motivationalQuote = quote.first,
                    quoteSource = quote.second,
                    completedHabitsCount = completedCount,
                    totalHabitsCount = fardhuPrayers.size
                )
            }
        }
    }

    /**
     * Start coroutine to update time and prayer status every minute.
     */
    private fun startTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentTime = getCurrentTime(),
                        prayerStatus = calculatePrayerStatus()
                    )
                }
                kotlinx.coroutines.delay(60000) // Update every minute
            }
        }
    }

    /**
     * Get current time formatted as HH:mm
     */
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }

    /**
     * Get current Gregorian date in Indonesian format
     */
    private fun getGregorianDate(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return dateFormat.format(Date())
    }

    /**
     * Get approximate Hijri date (simplified calculation).
     * For production, use a proper Hijri calendar library.
     */
    private fun getHijriDate(): String {
        // Simplified Hijri date - in production use proper library like Umm Al-Qura
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)

        // Approximate Hijri months (simplified)
        val hijriMonths = listOf(
            "Muharram", "Safar", "Rabiul Awal", "Rabiul Akhir",
            "Jumadil Awal", "Jumadil Akhir", "Rajab", "Sya'ban",
            "Ramadhan", "Syawal", "Dzulqa'dah", "Dzulhijjah"
        )

        // This is a placeholder - proper calculation needed
        val hijriMonth = hijriMonths[(month + 1) % 12]
        val hijriYear = 1446 // Current Hijri year approximation

        return "$dayOfMonth $hijriMonth $hijriYear H"
    }

    /**
     * Calculate current prayer status including countdown to next prayer.
     */
    private fun calculatePrayerStatus(): PrayerStatus {
        val calendar = Calendar.getInstance()
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        val prayers = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
        var currentPrayer = ""
        var nextPrayer = ""
        var countdownMinutes = 0

        for (i in prayers.indices) {
            val prayerTime = prayerSchedule[prayers[i]] ?: 0
            val nextPrayerTime = if (i < prayers.size - 1) {
                prayerSchedule[prayers[i + 1]] ?: 0
            } else {
                prayerSchedule["Subuh"]!! + (24 * 60) // Next day Subuh
            }

            if (currentMinutes >= prayerTime && currentMinutes < nextPrayerTime) {
                currentPrayer = prayers[i]
                nextPrayer = if (i < prayers.size - 1) prayers[i + 1] else "Subuh"
                countdownMinutes = nextPrayerTime - currentMinutes
                break
            }
        }

        // Handle time before Subuh
        if (currentPrayer.isEmpty()) {
            val subuhTime = prayerSchedule["Subuh"] ?: 0
            if (currentMinutes < subuhTime) {
                currentPrayer = "Isya"
                nextPrayer = "Subuh"
                countdownMinutes = subuhTime - currentMinutes
            }
        }

        return PrayerStatus(
            currentPrayerName = currentPrayer,
            nextPrayerName = nextPrayer,
            countdownMinutes = countdownMinutes,
            isCurrentPrayerActive = true
        )
    }

    /**
     * Get random motivational quote from collection.
     */
    private fun getRandomMotivationalQuote(): Pair<String, String> {
        val quotes = listOf(
            Pair(
                "Dan bersegeralah kamu kepada ampunan dari Tuhanmu dan kepada surga yang luasnya seluas langit dan bumi yang disediakan untuk orang-orang yang bertakwa.",
                "- QS. Ali Imran: 133"
            ),
            Pair(
                "Sesungguhnya shalat itu mencegah dari perbuatan keji dan mungkar.",
                "- QS. Al-Ankabut: 45"
            ),
            Pair(
                "Hai orang-orang yang beriman, jadikanlah sabar dan shalat sebagai penolongmu.",
                "- QS. Al-Baqarah: 153"
            ),
            Pair(
                "Bacalah Al-Quran, karena ia akan datang pada hari kiamat sebagai pemberi syafaat bagi para pembacanya.",
                "- HR. Muslim"
            ),
            Pair(
                "Puasa adalah perisai, maka janganlah berkata kotor dan janganlah berbuat bodoh.",
                "- HR. Bukhari"
            )
        )
        return quotes.random()
    }

    /**
     * Get list of Fardhu (obligatory) prayers with their times.
     */
    private fun getFardhuPrayerTimes(): List<PrayerTime> {
        return listOf(
            PrayerTime(
                name = "Subuh",
                time = "04:30",
                icon = Icons.Outlined.WbTwilight,
                isCompleted = false,
                notificationEnabled = true,
                timeInMinutes = 4 * 60 + 30
            ),
            PrayerTime(
                name = "Dzuhur",
                time = "11:55",
                icon = Icons.Outlined.WbSunny,
                isCompleted = false,
                notificationEnabled = true,
                timeInMinutes = 11 * 60 + 55
            ),
            PrayerTime(
                name = "Ashar",
                time = "15:10",
                icon = Icons.Outlined.WbCloudy,
                isCompleted = false,
                notificationEnabled = true,
                timeInMinutes = 15 * 60 + 10
            ),
            PrayerTime(
                name = "Maghrib",
                time = "18:00",
                icon = Icons.Outlined.Brightness3,
                isCompleted = false,
                notificationEnabled = true,
                timeInMinutes = 18 * 60
            ),
            PrayerTime(
                name = "Isya",
                time = "19:15",
                icon = Icons.Outlined.NightsStay,
                isCompleted = false,
                notificationEnabled = true,
                timeInMinutes = 19 * 60 + 15
            )
        )
    }

    /**
     * Get list of Sunnah (voluntary) prayers with their times.
     */
    private fun getSunnahPrayerTimes(): List<PrayerTime> {
        return listOf(
            PrayerTime(
                name = "Dhuha",
                time = "06:00 - 11:00",
                icon = Icons.Outlined.WbSunny,
                isCompleted = false,
                notificationEnabled = true,
                timeInMinutes = 6 * 60
            ),
            PrayerTime(
                name = "Qabliyah Dzuhur",
                time = "11:30",
                icon = Icons.Outlined.WbSunny,
                isCompleted = false,
                notificationEnabled = false,
                timeInMinutes = 11 * 60 + 30
            ),
            PrayerTime(
                name = "Ba'diyah Dzuhur",
                time = "12:15",
                icon = Icons.Outlined.WbSunny,
                isCompleted = false,
                notificationEnabled = false,
                timeInMinutes = 12 * 60 + 15
            ),
            PrayerTime(
                name = "Ba'diyah Maghrib",
                time = "18:20",
                icon = Icons.Outlined.Brightness3,
                isCompleted = false,
                notificationEnabled = false,
                timeInMinutes = 18 * 60 + 20
            ),
            PrayerTime(
                name = "Ba'diyah Isya",
                time = "19:35",
                icon = Icons.Outlined.NightsStay,
                isCompleted = false,
                notificationEnabled = false,
                timeInMinutes = 19 * 60 + 35
            ),
            PrayerTime(
                name = "Tahajud",
                time = "03:00",
                icon = Icons.Outlined.NightsStay,
                isCompleted = false,
                notificationEnabled = true,
                timeInMinutes = 3 * 60
            ),
            PrayerTime(
                name = "Witir",
                time = "04:00",
                icon = Icons.Outlined.NightsStay,
                isCompleted = false,
                notificationEnabled = false,
                timeInMinutes = 4 * 60
            )
        )
    }

    // ========================================================================
    // PUBLIC FUNCTIONS - UI Events
    // ========================================================================

    /**
     * Handle prayer tab selection (Fardhu/Sunnah).
     */
    fun onPrayerTabSelected(tab: String) {
        viewModelScope.launch {
            val newPrayerTimes = if (tab == "fardhu") {
                getFardhuPrayerTimes()
            } else {
                getSunnahPrayerTimes()
            }

            _uiState.update { currentState ->
                currentState.copy(
                    selectedPrayerTab = tab,
                    prayerTimes = newPrayerTimes,
                    completedHabitsCount = newPrayerTimes.count { it.isCompleted },
                    totalHabitsCount = newPrayerTimes.size
                )
            }
        }
    }

    /**
     * Toggle prayer completion status.
     */
    fun togglePrayerCompletion(prayerName: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedPrayerTimes = currentState.prayerTimes.map { prayer ->
                    if (prayer.name == prayerName) {
                        prayer.copy(isCompleted = !prayer.isCompleted)
                    } else {
                        prayer
                    }
                }

                val completedCount = updatedPrayerTimes.count { it.isCompleted }

                // Update sholatCompleted based on Fardhu completion
                val allFardhuCompleted = if (currentState.selectedPrayerTab == "fardhu") {
                    updatedPrayerTimes.all { it.isCompleted }
                } else {
                    currentState.sholatCompleted
                }

                currentState.copy(
                    prayerTimes = updatedPrayerTimes,
                    completedHabitsCount = completedCount,
                    sholatCompleted = allFardhuCompleted
                )
            }
        }
    }

    /**
     * Toggle prayer notification status.
     */
    fun togglePrayerNotification(prayerName: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedPrayerTimes = currentState.prayerTimes.map { prayer ->
                    if (prayer.name == prayerName) {
                        prayer.copy(notificationEnabled = !prayer.notificationEnabled)
                    } else {
                        prayer
                    }
                }
                currentState.copy(prayerTimes = updatedPrayerTimes)
            }
        }
    }

    /**
     * Toggle quick access item completion (Sholat, Dzikir, Tilawah, Puasa).
     */
    fun toggleQuickAccessCompletion(type: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                when (type) {
                    "sholat" -> currentState.copy(sholatCompleted = !currentState.sholatCompleted)
                    "dzikir" -> currentState.copy(dzikirCompleted = !currentState.dzikirCompleted)
                    "tilawah" -> currentState.copy(tilawahCompleted = !currentState.tilawahCompleted)
                    "puasa" -> currentState.copy(puasaCompleted = !currentState.puasaCompleted)
                    else -> currentState
                }
            }
        }
    }

    // ========================================================================
    // HELPER FUNCTIONS
    // ========================================================================

    /**
     * Format countdown minutes to readable string.
     */
    fun formatCountdown(minutes: Int): String {
        return when {
            minutes >= 60 -> {
                val hours = minutes / 60
                val mins = minutes % 60
                if (mins > 0) "$hours jam $mins menit lagi" else "$hours jam lagi"
            }
            minutes > 0 -> "$minutes menit lagi"
            else -> "Waktu sholat"
        }
    }
}