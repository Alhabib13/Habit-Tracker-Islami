package com.islami.Aha.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Profile Screen - Layar untuk melihat profil dan pengaturan.
 *
 * Fitur:
 * - Informasi pengguna dan statistik ringkas
 * - Pengaturan notifikasi
 * - Pengaturan tema (mode gelap)
 * - Pengaturan format waktu
 * - Reset data
 * - Logout
 *
 * @param viewModel ViewModel untuk mengelola state profil
 * @param onNavigateBack Callback untuk kembali ke layar sebelumnya
 * @param onNavigateToHome Callback navigasi ke Home
 * @param onNavigateToStatistic Callback navigasi ke Statistik
 * @param onNavigateToAddHabit Callback navigasi ke Tambah Habit
 * @param onNavigateToNotification Callback navigasi ke Notifikasi
 * @param onLogout Callback untuk logout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToStatistic: () -> Unit = {},
    onNavigateToAddHabit: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreenContent(
        uiState = uiState,
        onToggleNotification = viewModel::toggleNotification,
        onToggleDarkMode = viewModel::toggleDarkMode,
        onShowTimeFormatDialog = viewModel::showTimeFormatDialog,
        onHideTimeFormatDialog = viewModel::hideTimeFormatDialog,
        onSetTimeFormat = viewModel::setTimeFormat,
        onShowResetConfirmation = viewModel::showResetConfirmation,
        onHideResetConfirmation = viewModel::hideResetConfirmation,
        onConfirmReset = viewModel::resetAllData,
        onShowLogoutConfirmation = viewModel::showLogoutConfirmation,
        onHideLogoutConfirmation = viewModel::hideLogoutConfirmation,
        onConfirmLogout = {
            if (viewModel.logout()) {
                onLogout()
            }
        },
        onNavigateBack = onNavigateBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToStatistic = onNavigateToStatistic,
        onNavigateToAddHabit = onNavigateToAddHabit,
        onNavigateToNotification = onNavigateToNotification
    )
}

/**
 * Konten Profile Screen - Stateless composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onToggleNotification: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onShowTimeFormatDialog: () -> Unit,
    onHideTimeFormatDialog: () -> Unit,
    onSetTimeFormat: (TimeFormat) -> Unit,
    onShowResetConfirmation: () -> Unit,
    onHideResetConfirmation: () -> Unit,
    onConfirmReset: () -> Unit,
    onShowLogoutConfirmation: () -> Unit,
    onHideLogoutConfirmation: () -> Unit,
    onConfirmLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToStatistic: () -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToNotification: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil",
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
                currentRoute = "profile",
                onItemSelected = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "statistic" -> onNavigateToStatistic()
                        "add_habit" -> onNavigateToAddHabit()
                        "notification" -> onNavigateToNotification()
                        "profile" -> { /* Already here */ }
                    }
                }
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (uiState.isLoading) {
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
                // Profile Header
                item {
                    ProfileHeaderCard(
                        userInfo = uiState.userInfo,
                        totalHabits = uiState.totalHabits,
                        totalCompleted = uiState.totalCompleted,
                        currentStreak = uiState.currentStreak
                    )
                }

                // Settings Section
                item {
                    SettingsSectionTitle(title = "Pengaturan")
                }

                // Notification Setting
                item {
                    SettingsToggleItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Notifikasi",
                        subtitle = "Terima pengingat ibadah",
                        isChecked = uiState.notificationEnabled,
                        onToggle = onToggleNotification
                    )
                }

                // Dark Mode Setting
                item {
                    SettingsToggleItem(
                        icon = Icons.Outlined.DarkMode,
                        title = "Mode Gelap",
                        subtitle = "Gunakan tema gelap",
                        isChecked = uiState.darkModeEnabled,
                        onToggle = onToggleDarkMode
                    )
                }

                // Time Format Setting
                item {
                    SettingsClickItem(
                        icon = Icons.Outlined.Schedule,
                        title = "Format Waktu",
                        subtitle = uiState.timeFormat.displayName,
                        onClick = onShowTimeFormatDialog
                    )
                }

                // Data Section
                item {
                    SettingsSectionTitle(title = "Data")
                }

                // Reset Data
                item {
                    SettingsClickItem(
                        icon = Icons.Outlined.RestartAlt,
                        title = "Reset Data",
                        subtitle = "Hapus semua data kebiasaan",
                        onClick = onShowResetConfirmation,
                        isDestructive = true
                    )
                }

                // Account Section (hanya jika sudah login)
                if (uiState.userInfo.isLoggedIn) {
                    item {
                        SettingsSectionTitle(title = "Akun")
                    }

                    item {
                        SettingsClickItem(
                            icon = Icons.Outlined.Logout,
                            title = "Keluar",
                            subtitle = "Keluar dari akun Anda",
                            onClick = onShowLogoutConfirmation,
                            isDestructive = true
                        )
                    }
                }

                // About Section
                item {
                    SettingsSectionTitle(title = "Tentang")
                }

                item {
                    SettingsInfoItem(
                        icon = Icons.Outlined.Info,
                        title = "Versi Aplikasi",
                        value = uiState.appVersion
                    )
                }

                item {
                    SettingsClickItem(
                        icon = Icons.Outlined.PrivacyTip,
                        title = "Kebijakan Privasi",
                        subtitle = "Baca kebijakan privasi kami",
                        onClick = { /* TODO: Open privacy policy */ }
                    )
                }

                item {
                    SettingsClickItem(
                        icon = Icons.Outlined.Description,
                        title = "Syarat & Ketentuan",
                        subtitle = "Baca syarat penggunaan",
                        onClick = { /* TODO: Open terms */ }
                    )
                }

                // Footer
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aha - Aplikasi Ibadah Harian\nDibuat dengan penuh cinta",
                            fontSize = 12.sp,
                            color = Gray400,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Time Format Dialog
    if (uiState.showTimeFormatDialog) {
        TimeFormatDialog(
            currentFormat = uiState.timeFormat,
            onSelect = onSetTimeFormat,
            onDismiss = onHideTimeFormatDialog
        )
    }

    // Reset Confirmation Dialog
    if (uiState.showResetConfirmation) {
        ConfirmationDialog(
            title = "Reset Data",
            message = "Apakah Anda yakin ingin menghapus semua data kebiasaan? Tindakan ini tidak dapat dibatalkan.",
            confirmText = "Reset",
            onConfirm = onConfirmReset,
            onDismiss = onHideResetConfirmation
        )
    }

    // Logout Confirmation Dialog
    if (uiState.showLogoutConfirmation) {
        ConfirmationDialog(
            title = "Keluar",
            message = "Apakah Anda yakin ingin keluar dari akun?",
            confirmText = "Keluar",
            onConfirm = onConfirmLogout,
            onDismiss = onHideLogoutConfirmation
        )
    }
}

/**
 * Card header profil dengan info pengguna dan statistik.
 */
@Composable
fun ProfileHeaderCard(
    userInfo: UserInfo,
    totalHabits: Int,
    totalCompleted: Int,
    currentStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(SurfaceWhite),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInfo.avatarInitial,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name
            Text(
                text = userInfo.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceWhite
            )

            // Email
            if (userInfo.email.isNotEmpty()) {
                Text(
                    text = userInfo.email,
                    fontSize = 14.sp,
                    color = SurfaceWhite.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(
                    value = "$totalHabits",
                    label = "Kebiasaan"
                )
                ProfileStat(
                    value = "$totalCompleted",
                    label = "Selesai"
                )
                ProfileStat(
                    value = "$currentStreak",
                    label = "Hari Streak"
                )
            }
        }
    }
}

/**
 * Statistik profil.
 */
@Composable
fun ProfileStat(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SurfaceWhite
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = SurfaceWhite.copy(alpha = 0.8f)
        )
    }
}

/**
 * Judul section pengaturan.
 */
@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Gray500,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

/**
 * Item pengaturan dengan toggle.
 */
@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray900
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Gray500
                )
            }

            // Switch
            Switch(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceWhite,
                    checkedTrackColor = GreenPrimary,
                    uncheckedThumbColor = SurfaceWhite,
                    uncheckedTrackColor = Gray300
                )
            )
        }
    }
}

/**
 * Item pengaturan yang bisa diklik.
 */
@Composable
fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) ErrorRed else Gray900
    val iconBackgroundColor = if (isDestructive) ErrorRed.copy(alpha = 0.1f) else GreenPrimary.copy(alpha = 0.1f)
    val iconTint = if (isDestructive) ErrorRed else GreenPrimary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Gray500
                )
            }

            // Chevron
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Gray400,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Item pengaturan untuk menampilkan info.
 */
@Composable
fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Gray900,
                modifier = Modifier.weight(1f)
            )

            // Value
            Text(
                text = value,
                fontSize = 14.sp,
                color = Gray500
            )
        }
    }
}

/**
 * Dialog untuk memilih format waktu.
 */
@Composable
fun TimeFormatDialog(
    currentFormat: TimeFormat,
    onSelect: (TimeFormat) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Format Waktu",
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        },
        text = {
            Column {
                TimeFormat.entries.forEach { format ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(format) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = format == currentFormat,
                            onClick = { onSelect(format) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = GreenPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = format.displayName,
                            fontSize = 14.sp,
                            color = Gray900
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = Gray500
                )
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Dialog konfirmasi generik.
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Gray700
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = ErrorRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = Gray500
                )
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(20.dp)
    )
}

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    HabitIslamiTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(
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
            ),
            onToggleNotification = {},
            onToggleDarkMode = {},
            onShowTimeFormatDialog = {},
            onHideTimeFormatDialog = {},
            onSetTimeFormat = {},
            onShowResetConfirmation = {},
            onHideResetConfirmation = {},
            onConfirmReset = {},
            onShowLogoutConfirmation = {},
            onHideLogoutConfirmation = {},
            onConfirmLogout = {},
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToStatistic = {},
            onNavigateToAddHabit = {},
            onNavigateToNotification = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Profile Guest")
@Composable
fun ProfileScreenGuestPreview() {
    HabitIslamiTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(
                isLoading = false,
                userInfo = UserInfo(
                    name = "Pengguna Lokal",
                    email = "",
                    avatarInitial = "P",
                    isLoggedIn = false
                ),
                notificationEnabled = true,
                darkModeEnabled = true,
                timeFormat = TimeFormat.HOUR_12,
                totalHabits = 5,
                totalCompleted = 42,
                currentStreak = 3,
                appVersion = "1.0.0"
            ),
            onToggleNotification = {},
            onToggleDarkMode = {},
            onShowTimeFormatDialog = {},
            onHideTimeFormatDialog = {},
            onSetTimeFormat = {},
            onShowResetConfirmation = {},
            onHideResetConfirmation = {},
            onConfirmReset = {},
            onShowLogoutConfirmation = {},
            onHideLogoutConfirmation = {},
            onConfirmLogout = {},
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToStatistic = {},
            onNavigateToAddHabit = {},
            onNavigateToNotification = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Profile Loading")
@Composable
fun ProfileScreenLoadingPreview() {
    HabitIslamiTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(isLoading = true),
            onToggleNotification = {},
            onToggleDarkMode = {},
            onShowTimeFormatDialog = {},
            onHideTimeFormatDialog = {},
            onSetTimeFormat = {},
            onShowResetConfirmation = {},
            onHideResetConfirmation = {},
            onConfirmReset = {},
            onShowLogoutConfirmation = {},
            onHideLogoutConfirmation = {},
            onConfirmLogout = {},
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToStatistic = {},
            onNavigateToAddHabit = {},
            onNavigateToNotification = {}
        )
    }
}
