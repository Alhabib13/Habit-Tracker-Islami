package com.islami.Aha.ui.home

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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val currentTime: String = "",
    val location: String = "Jakarta",
    val gregorianDate: String = "",
    val hijriDate: String = "",
    val userName: String = "Ahmad",
    val nextPrayerName: String = "",
    val nextPrayerTimeRemaining: String = "",
    val nextPrayerProgress: Float = 0f,
    val selectedMainCategory: String = "Sholat",
    val selectedSubTabIndex: Int = 0,
    val allHabits: List<Habit> = emptyList(),
    val motivationalQuote: String = "",
    val quoteSource: String = ""
) {
    val subTabCategories: List<String>
        get() = when (selectedMainCategory) {
            "Sholat" -> listOf("Sholat Fardhu", "Sholat Sunnah")
            "Puasa" -> listOf("Puasa Wajib", "Puasa Sunnah")
            else -> emptyList()
        }

    val subTabDisplayNames: List<String>
        get() = when (selectedMainCategory) {
            "Sholat" -> listOf("Sholat Fardhu", "Sholat Sunnah")
            "Puasa" -> listOf("Puasa Wajib (Ramadan)", "Puasa Sunnah")
            else -> emptyList()
        }

    val isComingSoon: Boolean
        get() = selectedMainCategory in listOf("Dzikir", "Tilawah")

    val filteredHabits: List<Habit>
        get() {
            if (isComingSoon) return emptyList()
            val subCategory = subTabCategories.getOrNull(selectedSubTabIndex) ?: return emptyList()
            return allHabits.filter { it.category == subCategory }
        }

    val completedHabitsCount: Int
        get() = filteredHabits.count { it.isCompleted }

    val totalHabitsCount: Int
        get() = filteredHabits.size

    fun getCategoryBadge(mainCategory: String): String {
        return when (mainCategory) {
            "Sholat" -> {
                val habits = allHabits.filter { it.category.startsWith("Sholat") }
                "${habits.count { it.isCompleted }}/${habits.size}"
            }
            "Puasa" -> {
                val habits = allHabits.filter { it.category.startsWith("Puasa") }
                "${habits.count { it.isCompleted }}/${habits.size}"
            }
            else -> "Segera"
        }
    }
}

data class PrayerTimeInfo(
    val name: String,
    val hour: Int,
    val minute: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val habitDao: HabitDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val prayerTimes = listOf(
        PrayerTimeInfo("Subuh", 4, 30),
        PrayerTimeInfo("Dzuhur", 11, 55),
        PrayerTimeInfo("Ashar", 15, 10),
        PrayerTimeInfo("Maghrib", 18, 0),
        PrayerTimeInfo("Isya", 19, 15)
    )

    init {
        seedDataIfNeeded()
        loadHabits()
        startTimeUpdates()
        loadQuote()
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
        // Sholat Fardhu
        Habit(name = "Sholat Subuh", category = "Sholat Fardhu", icon = "\uD83C\uDF05", description = "", time = "04:30"),
        Habit(name = "Sholat Dzuhur", category = "Sholat Fardhu", icon = "\u2600\uFE0F", description = "", time = "11:55"),
        Habit(name = "Sholat Ashar", category = "Sholat Fardhu", icon = "\u2601\uFE0F", description = "", time = "15:10"),
        Habit(name = "Sholat Maghrib", category = "Sholat Fardhu", icon = "\uD83C\uDF19", description = "", time = "18:00"),
        Habit(name = "Sholat Isya", category = "Sholat Fardhu", icon = "\uD83C\uDF1C", description = "", time = "19:15"),
        // Sholat Sunnah
        Habit(name = "Sholat Dhuha", category = "Sholat Sunnah", icon = "\u2600\uFE0F", description = "06:00 - 11:00", time = "06:00"),
        Habit(name = "Qabliyah Dzuhur", category = "Sholat Sunnah", icon = "\u2600\uFE0F", description = "", time = "11:30"),
        Habit(name = "Ba'diyah Dzuhur", category = "Sholat Sunnah", icon = "\u2600\uFE0F", description = "", time = "12:15"),
        Habit(name = "Ba'diyah Maghrib", category = "Sholat Sunnah", icon = "\uD83C\uDF19", description = "", time = "18:20"),
        Habit(name = "Ba'diyah Isya", category = "Sholat Sunnah", icon = "\uD83C\uDF1C", description = "", time = "19:35"),
        Habit(name = "Tahajud", category = "Sholat Sunnah", icon = "\uD83C\uDF19", description = "", time = "03:00"),
        Habit(name = "Witir", category = "Sholat Sunnah", icon = "\uD83C\uDF19", description = "", time = "03:30"),
        // Puasa Wajib
        Habit(name = "Puasa Ramadan", category = "Puasa Wajib", icon = "\uD83C\uDF7D\uFE0F", description = "Sahur - Maghrib", time = ""),
        // Puasa Sunnah
        Habit(name = "Puasa Senin", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "Setiap Senin", time = ""),
        Habit(name = "Puasa Kamis", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "Setiap Kamis", time = ""),
        Habit(name = "Puasa Ayyamul Bidh", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "13-15 Hijriah", time = ""),
        Habit(name = "Puasa Daud", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "Selang-seling", time = ""),
        Habit(name = "Puasa Syawal", category = "Puasa Sunnah", icon = "\uD83C\uDF19", description = "6 hari di bulan Syawal", time = "")
    )

    private fun loadHabits() {
        viewModelScope.launch {
            habitDao.getHabits().collect { habits ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        allHabits = habits,
                        gregorianDate = getGregorianDate(),
                        hijriDate = getHijriDate()
                    )
                }
            }
        }
    }

    private fun startTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                val now = Calendar.getInstance()
                val prayerInfo = calculateNextPrayer(now)
                _uiState.update {
                    it.copy(
                        currentTime = getCurrentTime(),
                        nextPrayerName = prayerInfo.first,
                        nextPrayerTimeRemaining = prayerInfo.second,
                        nextPrayerProgress = prayerInfo.third
                    )
                }
                kotlinx.coroutines.delay(30000)
            }
        }
    }

    private fun calculateNextPrayer(now: Calendar): Triple<String, String, Float> {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        for (i in prayerTimes.indices) {
            val prayerMinutes = prayerTimes[i].hour * 60 + prayerTimes[i].minute
            if (currentMinutes < prayerMinutes) {
                val remaining = prayerMinutes - currentMinutes
                val hours = remaining / 60
                val minutes = remaining % 60
                val timeText = if (hours > 0) "$hours jam $minutes menit lagi" else "$minutes menit lagi"

                val prevPrayerMinutes = if (i > 0) {
                    prayerTimes[i - 1].hour * 60 + prayerTimes[i - 1].minute
                } else {
                    0
                }
                val totalInterval = prayerMinutes - prevPrayerMinutes
                val elapsed = currentMinutes - prevPrayerMinutes
                val progress = if (totalInterval > 0) elapsed.toFloat() / totalInterval else 0f

                return Triple(prayerTimes[i].name, timeText, progress.coerceIn(0f, 1f))
            }
        }

        // All prayers have passed, next is Subuh tomorrow
        val subuhMinutes = prayerTimes[0].hour * 60 + prayerTimes[0].minute
        val remaining = (24 * 60 - currentMinutes) + subuhMinutes
        val hours = remaining / 60
        val minutes = remaining % 60
        val timeText = "$hours jam $minutes menit lagi"
        return Triple("Subuh", timeText, 0.9f)
    }

    private fun loadQuote() {
        val quote = getRandomMotivationalQuote()
        _uiState.update { it.copy(motivationalQuote = quote.first, quoteSource = quote.second) }
    }

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            val updatedHabit = habit.copy(isCompleted = !habit.isCompleted)
            habitDao.updateHabit(updatedHabit)
        }
    }

    fun selectMainCategory(category: String) {
        _uiState.update { it.copy(selectedMainCategory = category, selectedSubTabIndex = 0) }
    }

    fun selectSubTab(index: Int) {
        _uiState.update { it.copy(selectedSubTabIndex = index) }
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }

    private fun getGregorianDate(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return dateFormat.format(Date())
    }

    private fun getHijriDate(): String {
        return "1 Ramadhan 1445 H"
    }

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
                "Amalan yang paling dicintai oleh Allah adalah yang paling konsisten meskipun sedikit.",
                "- HR. Bukhari & Muslim"
            )
        )
        return quotes.random()
    }
}
