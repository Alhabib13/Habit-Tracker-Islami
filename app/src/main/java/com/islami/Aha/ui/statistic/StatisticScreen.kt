package com.islami.Aha.ui.statistic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islami.Aha.ui.components.AhaBottomNavBar
import com.islami.Aha.ui.theme.*

/**
 * Statistic Screen - Layar untuk melihat statistik ibadah.
 *
 * Menampilkan:
 * - Ringkasan harian
 * - Progress mingguan
 * - Statistik per kategori
 * - Streak dan pencapaian
 *
 * @param viewModel ViewModel untuk mengelola state statistik
 * @param onNavigateBack Callback untuk kembali
 * @param onNavigateToHome Callback navigasi ke Home
 * @param onNavigateToAddHabit Callback navigasi ke Tambah Habit
 * @param onNavigateToNotification Callback navigasi ke Notifikasi
 * @param onNavigateToProfile Callback navigasi ke Profil
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    viewModel: StatisticViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToAddHabit: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    StatisticScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToAddHabit = onNavigateToAddHabit,
        onNavigateToNotification = onNavigateToNotification,
        onNavigateToProfile = onNavigateToProfile
    )
}

/**
 * Konten Statistic Screen - Stateless composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreenContent(
    uiState: StatisticUiState,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistik",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Gray900
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        },
        bottomBar = {
            AhaBottomNavBar(
                currentRoute = "statistic",
                onItemSelected = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "statistic" -> { /* Already here */ }
                        "add_habit" -> onNavigateToAddHabit()
                        "notification" -> onNavigateToNotification()
                        "profile" -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (uiState.isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header dengan tanggal
                item {
                    Text(
                        text = uiState.currentDate,
                        fontSize = 14.sp,
                        color = Gray500
                    )
                }

                // Card Progress Hari Ini
                item {
                    TodayProgressCard(
                        completed = uiState.todayCompleted,
                        total = uiState.todayTotal,
                        percentage = uiState.todayPercentage
                    )
                }

                // Statistik Mingguan
                item {
                    if (uiState.weeklyStats.isEmpty()) {
                        EmptyStatisticCard(
                            title = "Progress Minggu Ini",
                            message = "Belum ada data statistik mingguan"
                        )
                    } else {
                        WeeklyStatsCard(weeklyStats = uiState.weeklyStats)
                    }
                }

                // Streak Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StreakCard(
                            title = "Streak Saat Ini",
                            value = uiState.currentStreak,
                            icon = Icons.Outlined.LocalFireDepartment,
                            modifier = Modifier.weight(1f)
                        )
                        StreakCard(
                            title = "Streak Terpanjang",
                            value = uiState.longestStreak,
                            icon = Icons.Outlined.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Judul Kategori
                item {
                    Text(
                        text = "Statistik per Kategori",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Kategori Cards
                if (uiState.categoryStats.isEmpty()) {
                    item {
                        EmptyStatisticCard(
                            title = "Statistik Kategori",
                            message = "Belum ada data kategori"
                        )
                    }
                } else {
                    items(uiState.categoryStats) { category ->
                        CategoryStatCard(category = category)
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Card progress hari ini dengan circular progress indicator.
 */
@Composable
fun TodayProgressCard(
    completed: Int,
    total: Int,
    percentage: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Progress Hari Ini",
                    fontSize = 14.sp,
                    color = SurfaceWhite.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$completed dari $total",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ibadah selesai",
                    fontSize = 14.sp,
                    color = SurfaceWhite.copy(alpha = 0.8f)
                )
            }

            // Circular Progress
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.size(80.dp),
                    color = SurfaceWhite,
                    trackColor = SurfaceWhite.copy(alpha = 0.3f),
                    strokeWidth = 8.dp
                )
                Text(
                    text = "$percentage%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }
        }
    }
}

/**
 * Card statistik mingguan dengan bar chart sederhana.
 */
@Composable
fun WeeklyStatsCard(weeklyStats: List<DailyStatistic>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Progress Minggu Ini",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bar chart sederhana
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyStats.forEachIndexed { index, stat ->
                    val isToday = index == weeklyStats.size - 1

                    DayBar(
                        dayName = stat.dayName,
                        percentage = stat.percentage,
                        isToday = isToday,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Bar untuk satu hari dalam chart mingguan.
 */
@Composable
fun DayBar(
    dayName: String,
    percentage: Int,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedHeight by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 500),
        label = "barHeight"
    )

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Background bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    .background(Gray100)
            )
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedHeight)
                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    .background(if (isToday) GreenPrimary else GreenPrimary.copy(alpha = 0.6f))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day label
        Text(
            text = dayName,
            fontSize = 11.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) GreenPrimary else Gray500
        )
    }
}

/**
 * Card untuk menampilkan streak.
 */
@Composable
fun StreakCard(
    title: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (title.contains("Saat Ini")) WarningYellow else GreenPrimary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Gray500
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$value",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900
                    )
                    Text(
                        text = " hari",
                        fontSize = 14.sp,
                        color = Gray500,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Card untuk statistik per kategori.
 */
@Composable
fun CategoryStatCard(category: CategoryStatistic) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.icon,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = category.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray900
                        )
                        Text(
                            text = "${category.completedCount}/${category.totalCount} minggu ini",
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${category.percentage}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocalFireDepartment,
                            contentDescription = null,
                            tint = WarningYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = " ${category.streak} hari",
                            fontSize = 11.sp,
                            color = Gray500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { category.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GreenPrimary,
                trackColor = Gray100
            )
        }
    }
}

/**
 * Empty state card untuk statistik kosong.
 */
@Composable
fun EmptyStatisticCard(
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
            Text(
                text = message,
                fontSize = 13.sp,
                color = Gray400,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatisticScreenPreview() {
    HabitIslamiTheme {
        StatisticScreenContent(
            uiState = StatisticUiState(
                isLoading = false,
                currentDate = "Rabu, 05 Februari 2025",
                todayCompleted = 7,
                todayTotal = 10,
                todayPercentage = 70,
                weeklyStats = listOf(
                    DailyStatistic("Sen", "30", 8, 10, 80),
                    DailyStatistic("Sel", "31", 7, 10, 70),
                    DailyStatistic("Rab", "01", 9, 10, 90),
                    DailyStatistic("Kam", "02", 6, 10, 60),
                    DailyStatistic("Jum", "03", 10, 10, 100),
                    DailyStatistic("Sab", "04", 8, 10, 80),
                    DailyStatistic("Min", "05", 7, 10, 70)
                ),
                categoryStats = listOf(
                    CategoryStatistic("Sholat Fardhu", "🕌", 32, 35, 91, 7),
                    CategoryStatistic("Sholat Sunnah", "🤲", 18, 28, 64, 3),
                    CategoryStatistic("Dzikir", "📿", 5, 7, 71, 5),
                    CategoryStatistic("Tilawah", "📖", 4, 7, 57, 2)
                ),
                currentStreak = 7,
                longestStreak = 14,
                totalCompleted = 156
            ),
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToAddHabit = {},
            onNavigateToNotification = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodayProgressCardPreview() {
    HabitIslamiTheme {
        TodayProgressCard(
            completed = 7,
            total = 10,
            percentage = 70
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryStatCardPreview() {
    HabitIslamiTheme {
        CategoryStatCard(
            category = CategoryStatistic(
                name = "Sholat Fardhu",
                icon = "🕌",
                completedCount = 32,
                totalCount = 35,
                percentage = 91,
                streak = 7
            )
        )
    }
}
