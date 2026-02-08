package com.islami.Aha.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.islami.Aha.data.model.Habit
import com.islami.Aha.ui.theme.*

// Category card colors
private val TealStart = Color(0xFF0D9488)
private val TealEnd = Color(0xFF14B8A6)
private val PurpleStart = Color(0xFF7C3AED)
private val PurpleEnd = Color(0xFF8B5CF6)
private val AmberStart = Color(0xFFD97706)
private val AmberEnd = Color(0xFFFBBF24)

data class CategoryCardData(
    val name: String,
    val icon: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

private val categoryCards = listOf(
    CategoryCardData("Sholat", "\uD83D\uDD4C", EmeraldDark, Emerald),
    CategoryCardData("Dzikir", "\u2764\uFE0F", TealStart, TealEnd),
    CategoryCardData("Tilawah", "\uD83D\uDCD6", AmberStart, AmberEnd),
    CategoryCardData("Puasa", "\uD83C\uDF74", PurpleStart, PurpleEnd)
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddHabit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onToggleHabitCompletion = viewModel::toggleHabitCompletion,
        onNavigateToAddHabit = onNavigateToAddHabit,
        onSelectMainCategory = viewModel::selectMainCategory,
        onSelectSubTab = viewModel::selectSubTab
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onToggleHabitCompletion: (Habit) -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onSelectMainCategory: (String) -> Unit,
    onSelectSubTab: (Int) -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Emerald)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header with prayer info
        item {
            HomeHeader(
                userName = uiState.userName,
                currentTime = uiState.currentTime,
                gregorianDate = uiState.gregorianDate,
                hijriDate = uiState.hijriDate,
                location = uiState.location,
                nextPrayerName = uiState.nextPrayerName,
                nextPrayerTimeRemaining = uiState.nextPrayerTimeRemaining,
                nextPrayerProgress = uiState.nextPrayerProgress
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Category Cards
        item {
            CategoryCardsRow(
                selectedCategory = uiState.selectedMainCategory,
                onSelectCategory = onSelectMainCategory,
                getBadge = { uiState.getCategoryBadge(it) }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Habit Section Header
        item {
            HabitSectionHeader(
                completedCount = uiState.completedHabitsCount,
                totalCount = uiState.totalHabitsCount
            )
        }

        // Sub-tabs
        if (!uiState.isComingSoon && uiState.subTabDisplayNames.isNotEmpty()) {
            item {
                SubTabRow(
                    tabs = uiState.subTabDisplayNames,
                    selectedIndex = uiState.selectedSubTabIndex,
                    onSelectTab = onSelectSubTab
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Content based on category
        if (uiState.isComingSoon) {
            item {
                ComingSoonState(categoryName = uiState.selectedMainCategory)
            }
        } else if (uiState.filteredHabits.isEmpty()) {
            item {
                EmptyHabitState(onAddHabitClick = onNavigateToAddHabit)
            }
        } else {
            items(uiState.filteredHabits, key = { it.id }) { habit ->
                HomeHabitItem(
                    habit = habit,
                    onCheckedChange = { onToggleHabitCompletion(habit) },
                    currentTime = uiState.currentTime,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Motivational Quote
        item {
            IslamicMotivationCard(
                quote = uiState.motivationalQuote,
                source = uiState.quoteSource
            )
        }
    }
}

@Composable
fun HomeHeader(
    userName: String,
    currentTime: String,
    gregorianDate: String,
    hijriDate: String,
    location: String,
    nextPrayerName: String,
    nextPrayerTimeRemaining: String,
    nextPrayerProgress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(brush = Brush.verticalGradient(colors = listOf(EmeraldDark, Emerald)))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Assalamu'alaikum, $userName \uD83D\uDC4B",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentTime,
                fontSize = 48.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = gregorianDate, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                Text(text = "\u2022", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Text(text = hijriDate, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            }

            // Location
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\uD83D\uDCCD $location",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            // Next prayer info
            if (nextPrayerName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "\uD83D\uDD4C $nextPrayerName dalam $nextPrayerTimeRemaining",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { nextPrayerProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = GoldShimmer,
                            trackColor = Color.White.copy(alpha = 0.2f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCardsRow(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    getBadge: (String) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categoryCards.forEach { card ->
            val isSelected = card.name == selectedCategory
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                label = "categoryScale"
            )

            Card(
                modifier = Modifier
                    .width(100.dp)
                    .scale(scale)
                    .then(
                        if (isSelected) {
                            Modifier.border(2.dp, Gold, RoundedCornerShape(16.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSelectCategory(card.name) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (isSelected) 1f else 0.7f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(card.gradientStart, card.gradientEnd)
                            )
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = card.icon, fontSize = 28.sp)
                        Text(
                            text = card.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = getBadge(card.name),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onSelectTab(index) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) Emerald else Color.Transparent,
                border = if (!isSelected) {
                    androidx.compose.foundation.BorderStroke(1.dp, Emerald.copy(alpha = 0.5f))
                } else null
            ) {
                Text(
                    text = tab,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Emerald,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun HabitSectionHeader(completedCount: Int, totalCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Habit Hari Ini",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Surface(
            shape = RoundedCornerShape(50),
            color = GoldLight
        ) {
            Text(
                text = "$completedCount/$totalCount selesai",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
fun HomeHabitItem(
    habit: Habit,
    onCheckedChange: (Boolean) -> Unit,
    currentTime: String,
    modifier: Modifier = Modifier
) {
    // Determine if this is the current prayer time
    val isCurrentPrayer = isCurrentPrayerTime(habit.time, currentTime)

    val backgroundColor by animateColorAsState(
        targetValue = when {
            habit.isCompleted -> MaterialTheme.colorScheme.primaryContainer
            isCurrentPrayer -> EmeraldLight
            else -> MaterialTheme.colorScheme.surface
        },
        label = "bgColor"
    )
    val checkScale by animateFloatAsState(
        targetValue = if (habit.isCompleted) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "checkScale"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (habit.isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left accent bar for current prayer
            if (isCurrentPrayer && !habit.isCompleted) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(Emerald)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (habit.isCompleted) Emerald.copy(alpha = 0.15f) else EmeraldLight,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = habit.icon, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name + Time
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (habit.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (habit.time.isNotEmpty()) {
                        Text(
                            text = habit.time,
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    } else if (habit.description.isNotEmpty()) {
                        Text(
                            text = habit.description,
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                }

                // Bell icon
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Pengingat",
                    tint = if (habit.isReminderEnabled) Emerald else Gray400,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Circular checkbox
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .scale(checkScale)
                        .clip(CircleShape)
                        .then(
                            if (habit.isCompleted) {
                                Modifier.background(Emerald, CircleShape)
                            } else {
                                Modifier.border(2.dp, Emerald, CircleShape)
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onCheckedChange(!habit.isCompleted) },
                    contentAlignment = Alignment.Center
                ) {
                    if (habit.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selesai",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun isCurrentPrayerTime(habitTime: String, currentTime: String): Boolean {
    if (habitTime.isEmpty() || currentTime.isEmpty()) return false
    try {
        val habitParts = habitTime.split(":")
        val currentParts = currentTime.split(":")
        if (habitParts.size < 2 || currentParts.size < 2) return false

        val habitHour = habitParts[0].toInt()
        val habitMinute = habitParts[1].toInt()
        val currentHour = currentParts[0].toInt()
        val currentMinute = currentParts[1].toInt()

        val habitMinutes = habitHour * 60 + habitMinute
        val currentMinutes = currentHour * 60 + currentMinute

        // Consider current if within 30 minutes before or after the prayer time
        return currentMinutes in (habitMinutes - 15)..(habitMinutes + 30)
    } catch (_: Exception) {
        return false
    }
}

@Composable
fun ComingSoonState(categoryName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "\uD83D\uDE80", fontSize = 48.sp)
            Text(
                text = "Segera Hadir!",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Fitur $categoryName sedang dalam pengembangan.",
                fontSize = 14.sp,
                color = Gray500,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyHabitState(onAddHabitClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "\uD83D\uDCFF", fontSize = 48.sp)
            Text(
                text = "Belum Ada Kebiasaan",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Mulai lacak ibadah harian Anda dengan menambahkan kebiasaan baru.",
                fontSize = 14.sp,
                color = Gray500,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddHabitClick,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald)
            ) {
                Text("Tambah Kebiasaan Baru", color = Color.White)
            }
        }
    }
}

@Composable
fun IslamicMotivationCard(quote: String, source: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GoldLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "\"$quote\"",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = source,
                fontSize = 12.sp,
                color = Gray500,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HabitIslamiTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                userName = "Ahmad",
                currentTime = "14:30",
                gregorianDate = "23 Mei 2024",
                hijriDate = "15 Dzulqa'dah 1445 H",
                location = "Jakarta",
                nextPrayerName = "Ashar",
                nextPrayerTimeRemaining = "40 menit lagi",
                nextPrayerProgress = 0.6f,
                selectedMainCategory = "Sholat",
                selectedSubTabIndex = 0,
                allHabits = listOf(
                    Habit(1, "Sholat Subuh", "Sholat Fardhu", "\uD83C\uDF05", "", true, time = "04:30"),
                    Habit(2, "Sholat Dzuhur", "Sholat Fardhu", "\u2600\uFE0F", "", true, time = "11:55"),
                    Habit(3, "Sholat Ashar", "Sholat Fardhu", "\u2601\uFE0F", "", false, time = "15:10"),
                    Habit(4, "Sholat Maghrib", "Sholat Fardhu", "\uD83C\uDF19", "", false, time = "18:00"),
                    Habit(5, "Sholat Isya", "Sholat Fardhu", "\uD83C\uDF1C", "", false, time = "19:15")
                ),
                motivationalQuote = "Amalan yang paling dicintai oleh Allah adalah yang paling konsisten.",
                quoteSource = "- HR. Bukhari"
            ),
            onToggleHabitCompletion = {},
            onNavigateToAddHabit = {},
            onSelectMainCategory = {},
            onSelectSubTab = {}
        )
    }
}
