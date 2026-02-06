package com.islami.Aha.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islami.Aha.ui.components.AhaBottomNavBar
import com.islami.Aha.ui.theme.*

/**
 * Home Screen - Main entry composable with ViewModel injection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToAddHabit: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onPrayerTabSelected = { viewModel.onPrayerTabSelected(it) },
        onTogglePrayerCompletion = { viewModel.togglePrayerCompletion(it) },
        onTogglePrayerNotification = { viewModel.togglePrayerNotification(it) },
        onToggleQuickAccess = { viewModel.toggleQuickAccessCompletion(it) },
        formatCountdown = { viewModel.formatCountdown(it) },
        onNavigateToAddHabit = onNavigateToAddHabit,
        onNavigateToNotification = onNavigateToNotification,
        onNavigateToStatistics = onNavigateToStatistics,
        onNavigateToProfile = onNavigateToProfile
    )
}

/**
 * Home Screen Content - Stateless composable containing all UI elements.
 * All state is passed from the ViewModel through parameters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onPrayerTabSelected: (String) -> Unit,
    onTogglePrayerCompletion: (String) -> Unit,
    onTogglePrayerNotification: (String) -> Unit,
    onToggleQuickAccess: (String) -> Unit,
    formatCountdown: (Int) -> String,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    // Show loading indicator while data is being loaded
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = GreenPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    // App icon with green background
                    Surface(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = GreenPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mosque,
                            contentDescription = "App Icon",
                            tint = SurfaceWhite,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddHabit) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Gray900,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = onNavigateToNotification) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Gray900,
                            modifier = Modifier.size(28.dp)
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
                currentRoute = "home",
                onItemSelected = { route ->
                    when (route) {
                        "home" -> { /* Already here */ }
                        "statistic" -> onNavigateToStatistics()
                        "add_habit" -> onNavigateToAddHabit()
                        "notification" -> onNavigateToNotification()
                        "profile" -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = SurfaceWhite
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Greeting with dynamic prayer countdown
            item {
                GreetingSection(
                    currentTime = uiState.currentTime,
                    prayerStatus = uiState.prayerStatus,
                    formatCountdown = formatCountdown
                )
            }

            // Location and date information
            item {
                LocationDateSection(
                    location = uiState.location,
                    hijriDate = uiState.hijriDate,
                    gregorianDate = uiState.gregorianDate
                )
            }

            // Quick access cards (Sholat, Dzikir, Tilawah, Puasa)
            item {
                QuickAccessCards(
                    sholatCompleted = uiState.sholatCompleted,
                    dzikirCompleted = uiState.dzikirCompleted,
                    tilawahCompleted = uiState.tilawahCompleted,
                    puasaCompleted = uiState.puasaCompleted,
                    onToggleQuickAccess = onToggleQuickAccess
                )
            }

            // Habit section header with tabs
            item {
                HabitHariIniSection(
                    completedHabits = uiState.completedHabitsCount,
                    totalHabits = uiState.totalHabitsCount,
                    selectedTab = uiState.selectedPrayerTab,
                    onTabSelected = onPrayerTabSelected
                )
            }

            // Prayer time items or empty state
            if (uiState.prayerTimes.isEmpty()) {
                item {
                    EmptyPrayerState()
                }
            } else {
                items(uiState.prayerTimes) { prayerTime ->
                    PrayerTimeItem(
                        prayerTime = prayerTime,
                        isCurrentPrayer = uiState.prayerStatus.currentPrayerName == prayerTime.name,
                        onCheckToggle = { onTogglePrayerCompletion(prayerTime.name) },
                        onNotificationToggle = { onTogglePrayerNotification(prayerTime.name) }
                    )
                }
            }

            // Motivational quote card
            item {
                IslamicMotivationCard(
                    quote = uiState.motivationalQuote,
                    source = uiState.quoteSource
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Greeting section displaying salaam, current time, and prayer countdown.
 */
@Composable
fun GreetingSection(
    currentTime: String,
    prayerStatus: PrayerStatus,
    formatCountdown: (Int) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Greeting text
        Text(
            text = "Assalamu'alaikum!",
            fontSize = 16.sp,
            color = Gray500,
            fontWeight = FontWeight.Normal
        )

        // Large time display
        Text(
            text = currentTime,
            fontSize = 56.sp,
            color = Gray900,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-2).sp
        )

        // Current prayer status with countdown
        Text(
            text = "${prayerStatus.nextPrayerName} • ${formatCountdown(prayerStatus.countdownMinutes)}",
            fontSize = 14.sp,
            color = Gray500,
            fontWeight = FontWeight.Normal
        )
    }
}

/**
 * Location and date section showing current location, Gregorian date, and Hijri date.
 */
@Composable
fun LocationDateSection(
    location: String,
    hijriDate: String,
    gregorianDate: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Location
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Gray500
        )
        Text(
            text = location,
            fontSize = 12.sp,
            color = Gray500
        )

        Text(text = "•", color = Gray500, fontSize = 12.sp)

        // Gregorian date
        Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Gray500
        )
        Text(
            text = gregorianDate,
            fontSize = 12.sp,
            color = Gray500
        )

        Text(text = "•", color = Gray500, fontSize = 12.sp)

        // Hijri date
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Gray500
        )
        Text(
            text = hijriDate,
            fontSize = 12.sp,
            color = Gray500
        )
    }
}

/**
 * Quick access cards grid for main habit categories.
 */
@Composable
fun QuickAccessCards(
    sholatCompleted: Boolean,
    dzikirCompleted: Boolean,
    tilawahCompleted: Boolean,
    puasaCompleted: Boolean,
    onToggleQuickAccess: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessCard(
                title = "Sholat",
                subtitle = if (sholatCompleted) "Selesai" else "Tercatat",
                icon = Icons.Outlined.Mosque,
                isCompleted = sholatCompleted,
                isPrimary = true,
                onClick = { onToggleQuickAccess("sholat") },
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                title = "Dzikir",
                subtitle = if (dzikirCompleted) "Selesai" else "Tercatat",
                icon = Icons.Outlined.FavoriteBorder,
                isCompleted = dzikirCompleted,
                isPrimary = false,
                onClick = { onToggleQuickAccess("dzikir") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessCard(
                title = "Tilawah",
                subtitle = if (tilawahCompleted) "Selesai" else "Tercatat",
                icon = Icons.Outlined.MenuBook,
                isCompleted = tilawahCompleted,
                isPrimary = false,
                onClick = { onToggleQuickAccess("tilawah") },
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                title = "Puasa",
                subtitle = if (puasaCompleted) "Selesai" else "Tercatat",
                icon = Icons.Default.Restaurant,
                isCompleted = puasaCompleted,
                isPrimary = false,
                onClick = { onToggleQuickAccess("puasa") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual quick access card component.
 *
 * @param title Card title (e.g., "Sholat")
 * @param subtitle Status text (e.g., "Tercatat" or "Selesai")
 * @param icon Icon to display
 * @param isCompleted Whether the habit is completed
 * @param isPrimary Whether this is the primary card (uses green background)
 * @param onClick Click handler
 * @param modifier Modifier for the card
 */
@Composable
fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isCompleted: Boolean,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Color logic based on completion and primary status
    val backgroundColor = when {
        isCompleted -> GreenPrimary
        isPrimary -> GreenPrimary
        else -> SurfaceWhite
    }
    val contentColor = when {
        isCompleted -> SurfaceWhite
        isPrimary -> SurfaceWhite
        else -> Gray900
    }
    val iconColor = when {
        isCompleted -> SurfaceWhite
        isPrimary -> SurfaceWhite
        else -> GreenPrimary
    }

    Card(
        modifier = modifier
            .height(110.dp)
            .shadow(
                elevation = if (isPrimary || isCompleted) 0.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with optional check overlay for completed state
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(36.dp),
                    tint = iconColor
                )
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd),
                        tint = SurfaceWhite
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Section header for daily habits with progress indicator and filter tabs.
 */
@Composable
fun HabitHariIniSection(
    completedHabits: Int,
    totalHabits: Int,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title row with progress badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Habit Hari Ini",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )

            // Progress badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GreenPrimary
            ) {
                Text(
                    text = "$completedHabits/$totalHabits selesai",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurfaceWhite,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Filter tabs
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChipButton(
                text = "Sholat Fardhu",
                isSelected = selectedTab == "fardhu",
                onClick = { onTabSelected("fardhu") }
            )
            FilterChipButton(
                text = "Sholat Sunnah",
                isSelected = selectedTab == "sunnah",
                onClick = { onTabSelected("sunnah") }
            )
        }
    }
}

/**
 * Filter chip button for tab selection.
 */
@Composable
fun FilterChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) GreenPrimary else Gray100
    val textColor = if (isSelected) SurfaceWhite else Gray500

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

/**
 * Prayer time item card with completion and notification controls.
 *
 * Displays prayer with three possible states:
 * - Completed (green check)
 * - Active/Current (highlighted border)
 * - Upcoming (default state)
 *
 * @param prayerTime Prayer data including name, time, and status
 * @param isCurrentPrayer Whether this is the currently active prayer
 * @param onCheckToggle Callback when completion is toggled
 * @param onNotificationToggle Callback when notification is toggled
 */
@Composable
fun PrayerTimeItem(
    prayerTime: PrayerTime,
    isCurrentPrayer: Boolean,
    onCheckToggle: () -> Unit,
    onNotificationToggle: () -> Unit
) {
    // Determine card styling based on state
    val backgroundColor = when {
        prayerTime.isCompleted -> GreenLight.copy(alpha = 0.5f)
        isCurrentPrayer -> GreenLight
        else -> SurfaceWhite
    }
    val borderColor = when {
        prayerTime.isCompleted -> GreenPrimary
        isCurrentPrayer -> GreenPrimary
        else -> Gray200
    }
    val borderWidth = if (isCurrentPrayer || prayerTime.isCompleted) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon and prayer info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = prayerTime.icon,
                    contentDescription = prayerTime.name,
                    modifier = Modifier.size(28.dp),
                    tint = if (isCurrentPrayer) GreenPrimary else Gray500
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = prayerTime.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray900
                    )
                    Text(
                        text = prayerTime.time,
                        fontSize = 13.sp,
                        color = Gray500
                    )
                }
            }

            // Right side: Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notification toggle
                IconButton(
                    onClick = onNotificationToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (prayerTime.notificationEnabled)
                            Icons.Outlined.Notifications
                        else
                            Icons.Outlined.NotificationsOff,
                        contentDescription = "Notification",
                        tint = if (prayerTime.notificationEnabled) Gray500 else Gray400,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Completion toggle
                IconButton(
                    onClick = onCheckToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (prayerTime.isCompleted)
                            Icons.Default.CheckCircle
                        else
                            Icons.Outlined.CheckCircle,
                        contentDescription = "Check",
                        tint = if (prayerTime.isCompleted) GreenPrimary else Gray400,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

/**
 * Islamic motivation card displaying an inspirational quote.
 */
@Composable
fun IslamicMotivationCard(
    quote: String,
    source: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = GreenPrimary.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Motivasi Islami",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceWhite
            )

            Text(
                text = quote,
                fontSize = 13.sp,
                color = SurfaceWhite,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = source,
                fontSize = 11.sp,
                color = SurfaceWhite.copy(alpha = 0.95f),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


/**
 * Empty state component for prayer list.
 * Displayed when no prayers are available.
 */
@Composable
fun EmptyPrayerState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Gray100)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.EventBusy,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Gray400
            )
            Text(
                text = "Tidak ada jadwal sholat",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray500,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Data waktu sholat sedang dimuat",
                fontSize = 12.sp,
                color = Gray400,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================================
// ICON EXTENSION
// ============================================================================

/**
 * Extension property for Mosque icon.
 * Using Home icon as placeholder - replace with actual mosque icon from resources.
 */
val Icons.Outlined.Mosque: ImageVector
    get() = Icons.Outlined.Home

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HabitIslamiTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                currentTime = "14:30",
                location = "Jakarta",
                gregorianDate = "23 Mei 2024",
                hijriDate = "15 Dzulqa'dah 1445 H",
                sholatCompleted = false,
                dzikirCompleted = false,
                tilawahCompleted = false,
                puasaCompleted = false,
                completedHabitsCount = 2,
                totalHabitsCount = 5,
                selectedPrayerTab = "fardhu",
                prayerStatus = PrayerStatus(
                    currentPrayerName = "Ashar",
                    nextPrayerName = "Maghrib",
                    countdownMinutes = 45,
                    isCurrentPrayerActive = true
                ),
                prayerTimes = listOf(
                    PrayerTime("Subuh", "04:30", Icons.Outlined.WbTwilight, true, true, 270),
                    PrayerTime("Dzuhur", "11:55", Icons.Outlined.WbSunny, true, true, 715),
                    PrayerTime("Ashar", "15:10", Icons.Outlined.WbCloudy, false, true, 910),
                    PrayerTime("Maghrib", "18:00", Icons.Outlined.Brightness3, false, true, 1080),
                    PrayerTime("Isya", "19:15", Icons.Outlined.NightsStay, false, true, 1155)
                ),
                motivationalQuote = "Dan bersegeralah kamu kepada ampunan dari Tuhanmu dan kepada surga yang luasnya seluas langit dan bumi yang disediakan untuk orang-orang yang bertakwa.",
                quoteSource = "- QS. Ali Imran: 133"
            ),
            onPrayerTabSelected = {},
            onTogglePrayerCompletion = {},
            onTogglePrayerNotification = {},
            onToggleQuickAccess = {},
            formatCountdown = { minutes ->
                when {
                    minutes >= 60 -> "${minutes / 60} jam ${minutes % 60} menit lagi"
                    else -> "$minutes menit lagi"
                }
            },
            onNavigateToAddHabit = {},
            onNavigateToNotification = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuickAccessCardPreview() {
    HabitIslamiTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessCard(
                title = "Sholat",
                subtitle = "Tercatat",
                icon = Icons.Outlined.Mosque,
                isCompleted = false,
                isPrimary = true,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                title = "Dzikir",
                subtitle = "Selesai",
                icon = Icons.Outlined.FavoriteBorder,
                isCompleted = true,
                isPrimary = false,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrayerTimeItemPreview() {
    HabitIslamiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Completed prayer
            PrayerTimeItem(
                prayerTime = PrayerTime("Subuh", "04:30", Icons.Outlined.WbTwilight, true, true, 270),
                isCurrentPrayer = false,
                onCheckToggle = {},
                onNotificationToggle = {}
            )
            // Current/Active prayer
            PrayerTimeItem(
                prayerTime = PrayerTime("Ashar", "15:10", Icons.Outlined.WbCloudy, false, true, 910),
                isCurrentPrayer = true,
                onCheckToggle = {},
                onNotificationToggle = {}
            )
            // Upcoming prayer
            PrayerTimeItem(
                prayerTime = PrayerTime("Maghrib", "18:00", Icons.Outlined.Brightness3, false, true, 1080),
                isCurrentPrayer = false,
                onCheckToggle = {},
                onNotificationToggle = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IslamicMotivationCardPreview() {
    HabitIslamiTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            IslamicMotivationCard(
                quote = "Dan bersegeralah kamu kepada ampunan dari Tuhanmu dan kepada surga yang luasnya seluas langit dan bumi yang disediakan untuk orang-orang yang bertakwa.",
                source = "- QS. Ali Imran: 133"
            )
        }
    }
}
