package com.islami.Aha.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.islami.Aha.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreenContent(
        uiState = uiState,
        onNavigateToSettings = onNavigateToSettings,
        onShowLogoutConfirmation = viewModel::showLogoutConfirmation,
        onHideLogoutConfirmation = viewModel::hideLogoutConfirmation,
        onConfirmLogout = {
            if (viewModel.logout()) {
                onLogout()
            }
        }
    )
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onNavigateToSettings: () -> Unit,
    onShowLogoutConfirmation: () -> Unit,
    onHideLogoutConfirmation: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Emerald)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header
            item {
                ProfileHeader(
                    userInfo = uiState.userInfo,
                    totalHabits = uiState.totalHabits,
                    totalCompleted = uiState.totalCompleted,
                    currentStreak = uiState.currentStreak,
                    onSettingsClick = onNavigateToSettings
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Achievements Section
            item {
                Text(
                    text = "\uD83C\uDFC6 Pencapaian Saya",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Achievement grid - using fixed height grid inside LazyColumn
            item {
                AchievementsGrid(achievements = uiState.achievements)
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // Weekly Summary
            item {
                WeeklySummaryCard(summary = uiState.weeklySummary)
            }

            // Logout button
            if (uiState.userInfo.isLoggedIn) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = onShowLogoutConfirmation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = BorderStroke(1.dp, ErrorRed)
                    ) {
                        Icon(Icons.Outlined.Logout, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Keluar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (uiState.showLogoutConfirmation) {
            AlertDialog(
                onDismissRequest = onHideLogoutConfirmation,
                title = { Text("Keluar", fontWeight = FontWeight.SemiBold) },
                text = { Text("Yakin ingin keluar?") },
                confirmButton = { TextButton(onClick = onConfirmLogout) { Text("Keluar", color = ErrorRed) } },
                dismissButton = { TextButton(onClick = onHideLogoutConfirmation) { Text("Batal") } }
            )
        }
    }
}

@Composable
fun ProfileHeader(
    userInfo: UserInfo,
    totalHabits: Int,
    totalCompleted: Int,
    currentStreak: Int,
    onSettingsClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(EmeraldDark, Emerald)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 40.dp, bottom = 60.dp)
        ) {
            // Top settings icon
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(Icons.Outlined.Settings, "Pengaturan", tint = Color.White)
            }

            // Avatar, Name, Email
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 48.dp, end = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(3.dp, Color.White, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInfo.avatarInitial,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = userInfo.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (userInfo.email.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userInfo.email,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Floating stats card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(value = "$totalHabits", label = "Kebiasaan")
                ProfileStat(value = "$totalCompleted", label = "Selesai")
                ProfileStat(value = "$currentStreak", label = "Streak")
            }
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Emerald
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AchievementsGrid(achievements: List<Achievement>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Manual 2-column grid to avoid nested scrollable
        achievements.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (achievement.isUnlocked) 2.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.emoji,
                fontSize = 32.sp,
                modifier = Modifier.alpha(if (achievement.isUnlocked) 1f else 0.3f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = achievement.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = achievement.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (achievement.isUnlocked) {
                Text(
                    text = "\u2705 Tercapai!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Emerald
                )
            } else {
                Text(
                    text = "\uD83D\uDD12 Belum",
                    fontSize = 11.sp,
                    color = Gray500
                )
            }
        }
    }
}

@Composable
fun WeeklySummaryCard(summary: WeeklySummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "\uD83D\uDCCA Ringkasan Minggu Ini",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Ibadah Selesai row with progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ibadah Selesai",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val fraction = (summary.completionPercentage / 100f).coerceIn(0f, 1f)
                    if (fraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Emerald)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${summary.completionPercentage.toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Emerald
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hari Aktif
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hari Aktif",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${summary.activeDays}/${summary.totalDays} hari",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kategori Terbaik
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kategori Terbaik",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = summary.bestCategory,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    HabitIslamiTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(
                isLoading = false,
                userInfo = UserInfo("Ahmad Fauzi", "ahmad@fauzi.com", "AF", true),
                totalHabits = 18,
                totalCompleted = 3,
                currentStreak = 0,
                achievements = listOf(
                    Achievement("first_step", "\uD83C\uDF1F", "Langkah Pertama", "Selesaikan ibadah pertama", true, 1f),
                    Achievement("burning", "\uD83D\uDD25", "Semangat Membara", "Streak 7 hari berturut", false, 0f),
                    Achievement("consistent", "\u2B50", "Bintang Konsisten", "Streak 14 hari berturut", false, 0f),
                    Achievement("champion", "\uD83C\uDFC6", "Juara Istiqomah", "Streak 30 hari berturut", false, 0f),
                    Achievement("hundred", "\uD83D\uDCAF", "Seratus Ibadah", "100 ibadah total selesai", false, 0.03f),
                    Achievement("sharpshooter", "\uD83C\uDFAF", "Penembak Jitu", "Semua habit selesai 1 hari", false, 0f)
                ),
                weeklySummary = WeeklySummary(
                    completionPercentage = 17f,
                    activeDays = 1,
                    totalDays = 7,
                    bestCategory = "Sholat Fardhu"
                )
            ),
            onNavigateToSettings = {},
            onShowLogoutConfirmation = {},
            onHideLogoutConfirmation = {},
            onConfirmLogout = {}
        )
    }
}
