package com.islami.Aha.ui.statistic

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
 * Data class untuk statistik harian.
 *
 * @property dayName Nama hari (Sen, Sel, dst)
 * @property date Tanggal
 * @property completedCount Jumlah habit yang diselesaikan
 * @property totalCount Total habit
 * @property percentage Persentase penyelesaian (0-100)
 */
data class DailyStatistic(
    val dayName: String,
    val date: String,
    val completedCount: Int,
    val totalCount: Int,
    val percentage: Int
)

/**
 * Data class untuk statistik per kategori.
 *
 * @property name Nama kategori (Sholat, Dzikir, dst)
 * @property icon Emoji icon
 * @property completedCount Jumlah selesai minggu ini
 * @property totalCount Total target minggu ini
 * @property percentage Persentase penyelesaian
 * @property streak Hari berturut-turut aktif
 */
data class CategoryStatistic(
    val name: String,
    val icon: String,
    val completedCount: Int,
    val totalCount: Int,
    val percentage: Int,
    val streak: Int
)

/**
 * UI State untuk Statistic Screen.
 *
 * @property isLoading Menandakan data sedang dimuat
 * @property currentDate Tanggal saat ini
 * @property todayCompleted Jumlah habit selesai hari ini
 * @property todayTotal Total habit hari ini
 * @property todayPercentage Persentase hari ini
 * @property weeklyStats Statistik per hari dalam seminggu
 * @property categoryStats Statistik per kategori
 * @property currentStreak Hari berturut-turut aktif
 * @property longestStreak Streak terpanjang
 * @property totalCompleted Total habit selesai sepanjang waktu
 */
data class StatisticUiState(
    val isLoading: Boolean = true,
    val currentDate: String = "",
    val todayCompleted: Int = 0,
    val todayTotal: Int = 0,
    val todayPercentage: Int = 0,
    val weeklyStats: List<DailyStatistic> = emptyList(),
    val categoryStats: List<CategoryStatistic> = emptyList(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompleted: Int = 0
)

/**
 * ViewModel untuk Statistic Screen.
 *
 * Mengelola:
 * - Data statistik harian dan mingguan
 * - Statistik per kategori
 * - Streak dan pencapaian
 *
 * Catatan: Menggunakan data dummy untuk demo.
 * Di production, akan membaca dari database lokal.
 */
class StatisticViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticUiState())
    val uiState: StateFlow<StatisticUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    /**
     * Memuat semua data statistik.
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simulasi loading
            kotlinx.coroutines.delay(500)

            val currentDate = getCurrentDateFormatted()
            val weeklyStats = generateWeeklyStats()
            val categoryStats = generateCategoryStats()

            // Hitung today stats
            val todayStats = weeklyStats.lastOrNull()
            val todayCompleted = todayStats?.completedCount ?: 0
            val todayTotal = todayStats?.totalCount ?: 10
            val todayPercentage = todayStats?.percentage ?: 0

            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentDate = currentDate,
                    todayCompleted = todayCompleted,
                    todayTotal = todayTotal,
                    todayPercentage = todayPercentage,
                    weeklyStats = weeklyStats,
                    categoryStats = categoryStats,
                    currentStreak = 7,  // Dummy data
                    longestStreak = 14, // Dummy data
                    totalCompleted = 156 // Dummy data
                )
            }
        }
    }

    /**
     * Refresh data statistik.
     */
    fun refreshStatistics() {
        loadStatistics()
    }

    /**
     * Mendapatkan tanggal saat ini dalam format Indonesia.
     */
    private fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return dateFormat.format(Date())
    }

    /**
     * Generate statistik mingguan (dummy data).
     * Di production, akan membaca dari database.
     */
    private fun generateWeeklyStats(): List<DailyStatistic> {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale("id", "ID"))
        val dateFormat = SimpleDateFormat("dd", Locale("id", "ID"))

        // Mundur ke 6 hari lalu
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        val stats = mutableListOf<DailyStatistic>()

        // Dummy completion data untuk 7 hari
        val completions = listOf(8, 7, 9, 6, 10, 8, 7) // Hari ini = 7
        val total = 10

        for (i in 0..6) {
            val dayName = dayFormat.format(calendar.time)
            val date = dateFormat.format(calendar.time)
            val completed = completions[i]
            val percentage = (completed * 100) / total

            stats.add(
                DailyStatistic(
                    dayName = dayName,
                    date = date,
                    completedCount = completed,
                    totalCount = total,
                    percentage = percentage
                )
            )

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return stats
    }

    /**
     * Generate statistik per kategori (dummy data).
     */
    private fun generateCategoryStats(): List<CategoryStatistic> {
        return listOf(
            CategoryStatistic(
                name = "Sholat Fardhu",
                icon = "🕌",
                completedCount = 32,
                totalCount = 35,
                percentage = 91,
                streak = 7
            ),
            CategoryStatistic(
                name = "Sholat Sunnah",
                icon = "🤲",
                completedCount = 18,
                totalCount = 28,
                percentage = 64,
                streak = 3
            ),
            CategoryStatistic(
                name = "Dzikir",
                icon = "📿",
                completedCount = 5,
                totalCount = 7,
                percentage = 71,
                streak = 5
            ),
            CategoryStatistic(
                name = "Tilawah",
                icon = "📖",
                completedCount = 4,
                totalCount = 7,
                percentage = 57,
                streak = 2
            ),
            CategoryStatistic(
                name = "Puasa Sunnah",
                icon = "🌙",
                completedCount = 2,
                totalCount = 4,
                percentage = 50,
                streak = 1
            )
        )
    }
}
