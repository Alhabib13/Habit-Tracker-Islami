package com.islami.Aha.ui.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islami.Aha.ui.components.AhaBottomNavBar
import com.islami.Aha.ui.theme.*

/**
 * Notification Screen - Layar untuk mengelola pengingat ibadah.
 *
 * Fitur:
 * - Daftar semua pengingat
 * - Toggle aktif/nonaktif pengingat
 * - Hapus pengingat
 * - Toggle notifikasi global
 *
 * @param viewModel ViewModel untuk mengelola state notifikasi
 * @param onNavigateBack Callback untuk kembali ke layar sebelumnya
 * @param onNavigateToHome Callback navigasi ke Home
 * @param onNavigateToStatistic Callback navigasi ke Statistik
 * @param onNavigateToAddHabit Callback navigasi ke Tambah Habit
 * @param onNavigateToProfile Callback navigasi ke Profil
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToStatistic: () -> Unit = {},
    onNavigateToAddHabit: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationScreenContent(
        uiState = uiState,
        onToggleGlobalNotification = viewModel::toggleGlobalNotification,
        onToggleReminder = viewModel::toggleReminderEnabled,
        onDeleteClick = viewModel::showDeleteConfirmation,
        onConfirmDelete = viewModel::deleteReminder,
        onDismissDelete = viewModel::hideDeleteConfirmation,
        onNavigateBack = onNavigateBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToStatistic = onNavigateToStatistic,
        onNavigateToAddHabit = onNavigateToAddHabit,
        onNavigateToProfile = onNavigateToProfile
    )
}

/**
 * Konten Notification Screen - Stateless composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreenContent(
    uiState: NotificationUiState,
    onToggleGlobalNotification: () -> Unit,
    onToggleReminder: (String) -> Unit,
    onDeleteClick: (ReminderItem) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToStatistic: () -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengingat",
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
                currentRoute = "notification",
                onItemSelected = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "statistic" -> onNavigateToStatistic()
                        "add_habit" -> onNavigateToAddHabit()
                        "notification" -> { /* Already here */ }
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
        } else if (uiState.reminders.isEmpty()) {
            // Empty state
            EmptyNotificationState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Global notification toggle
                item {
                    GlobalNotificationCard(
                        isEnabled = uiState.globalNotificationEnabled,
                        onToggle = onToggleGlobalNotification
                    )
                }

                // Info text
                item {
                    Text(
                        text = "Pengingat Aktif",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray700,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                // Reminder list
                items(
                    items = uiState.reminders,
                    key = { it.id }
                ) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        globalEnabled = uiState.globalNotificationEnabled,
                        onToggle = { onToggleReminder(reminder.id) },
                        onDelete = { onDeleteClick(reminder) }
                    )
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation && uiState.reminderToDelete != null) {
        DeleteConfirmationDialog(
            reminderName = uiState.reminderToDelete.habitName,
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}

/**
 * Card untuk toggle notifikasi global.
 */
@Composable
fun GlobalNotificationCard(
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isEnabled) GreenPrimary else Gray400,
        label = "cardBackground"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = if (isEnabled)
                        Icons.Outlined.NotificationsActive
                    else
                        Icons.Outlined.NotificationsOff,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        text = if (isEnabled) "Notifikasi Aktif" else "Notifikasi Nonaktif",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                    Text(
                        text = if (isEnabled)
                            "Anda akan menerima pengingat ibadah"
                        else
                            "Semua pengingat dinonaktifkan",
                        fontSize = 12.sp,
                        color = SurfaceWhite.copy(alpha = 0.8f)
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GreenPrimary,
                    checkedTrackColor = SurfaceWhite,
                    uncheckedThumbColor = Gray400,
                    uncheckedTrackColor = SurfaceWhite.copy(alpha = 0.5f)
                )
            )
        }
    }
}

/**
 * Card untuk satu item pengingat.
 */
@Composable
fun ReminderCard(
    reminder: ReminderItem,
    globalEnabled: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isEffectivelyEnabled = globalEnabled && reminder.isEnabled
    val cardAlpha by animateFloatAsState(
        targetValue = if (isEffectivelyEnabled) 1f else 0.6f,
        label = "cardAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isEffectivelyEnabled)
                            GreenPrimary.copy(alpha = 0.1f)
                        else
                            Gray100
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reminder.habitIcon,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.habitName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEffectivelyEnabled) Gray900 else Gray500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isEffectivelyEnabled)
                            GreenPrimary.copy(alpha = 0.1f)
                        else
                            Gray100
                    ) {
                        Text(
                            text = reminder.time,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isEffectivelyEnabled) GreenPrimary else Gray500,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = reminder.days,
                        fontSize = 12.sp,
                        color = Gray500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Gray400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Toggle switch
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    enabled = globalEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SurfaceWhite,
                        checkedTrackColor = GreenPrimary,
                        uncheckedThumbColor = SurfaceWhite,
                        uncheckedTrackColor = Gray300,
                        disabledCheckedThumbColor = SurfaceWhite.copy(alpha = 0.5f),
                        disabledCheckedTrackColor = GreenPrimary.copy(alpha = 0.3f),
                        disabledUncheckedThumbColor = SurfaceWhite.copy(alpha = 0.5f),
                        disabledUncheckedTrackColor = Gray300.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

/**
 * State kosong ketika tidak ada pengingat.
 */
@Composable
fun EmptyNotificationState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.NotificationsOff,
            contentDescription = null,
            tint = Gray300,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Belum Ada Pengingat",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Gray700
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tambahkan kebiasaan ibadah baru\nuntuk mengaktifkan pengingat",
            fontSize = 14.sp,
            color = Gray500,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Dialog konfirmasi hapus pengingat.
 */
@Composable
fun DeleteConfirmationDialog(
    reminderName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Hapus Pengingat",
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        },
        text = {
            Text(
                text = "Apakah Anda yakin ingin menghapus pengingat \"$reminderName\"?",
                fontSize = 14.sp,
                color = Gray700
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Hapus",
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
fun NotificationScreenPreview() {
    HabitIslamiTheme {
        NotificationScreenContent(
            uiState = NotificationUiState(
                isLoading = false,
                globalNotificationEnabled = true,
                reminders = listOf(
                    ReminderItem("1", "Sholat Subuh", "🕌", "04:30", "Setiap hari", true, "Sholat"),
                    ReminderItem("2", "Sholat Dzuhur", "🕌", "12:00", "Setiap hari", true, "Sholat"),
                    ReminderItem("3", "Sholat Dhuha", "☀️", "08:00", "Sen-Jum", false, "Sholat"),
                    ReminderItem("4", "Dzikir Pagi", "📿", "05:30", "Setiap hari", true, "Dzikir"),
                    ReminderItem("5", "Tilawah", "📖", "20:00", "Setiap hari", true, "Tilawah")
                )
            ),
            onToggleGlobalNotification = {},
            onToggleReminder = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToStatistic = {},
            onNavigateToAddHabit = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Notification Disabled")
@Composable
fun NotificationScreenDisabledPreview() {
    HabitIslamiTheme {
        NotificationScreenContent(
            uiState = NotificationUiState(
                isLoading = false,
                globalNotificationEnabled = false,
                reminders = listOf(
                    ReminderItem("1", "Sholat Subuh", "🕌", "04:30", "Setiap hari", true, "Sholat"),
                    ReminderItem("2", "Sholat Dzuhur", "🕌", "12:00", "Setiap hari", true, "Sholat")
                )
            ),
            onToggleGlobalNotification = {},
            onToggleReminder = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToStatistic = {},
            onNavigateToAddHabit = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Notification Empty")
@Composable
fun NotificationScreenEmptyPreview() {
    HabitIslamiTheme {
        NotificationScreenContent(
            uiState = NotificationUiState(
                isLoading = false,
                globalNotificationEnabled = true,
                reminders = emptyList()
            ),
            onToggleGlobalNotification = {},
            onToggleReminder = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToStatistic = {},
            onNavigateToAddHabit = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Notification Loading")
@Composable
fun NotificationScreenLoadingPreview() {
    HabitIslamiTheme {
        NotificationScreenContent(
            uiState = NotificationUiState(
                isLoading = true
            ),
            onToggleGlobalNotification = {},
            onToggleReminder = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onNavigateBack = {},
            onNavigateToHome = {},
            onNavigateToStatistic = {},
            onNavigateToAddHabit = {},
            onNavigateToProfile = {}
        )
    }
}
