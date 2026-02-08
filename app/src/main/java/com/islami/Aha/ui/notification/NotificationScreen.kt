package com.islami.Aha.ui.notification

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.islami.Aha.data.model.Habit
import com.islami.Aha.ui.theme.*

@Composable
fun NotificationScreen(viewModel: NotificationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationScreenContent(
        uiState = uiState,
        onToggleGlobalNotification = viewModel::toggleGlobalNotification,
        onToggleReminder = viewModel::toggleReminderEnabled,
        onDeleteClick = viewModel::showDeleteConfirmation,
        onConfirmDelete = viewModel::deleteReminder,
        onDismissDelete = viewModel::hideDeleteConfirmation
    )
}

@Composable
fun NotificationScreenContent(
    uiState: NotificationUiState,
    onToggleGlobalNotification: () -> Unit,
    onToggleReminder: (Habit) -> Unit,
    onDeleteClick: (Habit) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Emerald)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gradient banner
                item {
                    GlobalNotificationBanner(
                        isEnabled = uiState.globalNotificationEnabled,
                        onToggle = onToggleGlobalNotification
                    )
                }

                item {
                    Text(
                        text = "Pengingat Ibadah",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray700,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                if (uiState.habits.isEmpty()) {
                    item {
                        EmptyNotificationState(modifier = Modifier.fillParentMaxHeight(0.5f))
                    }
                } else {
                    items(uiState.habits, key = { it.id }) { habit ->
                        HabitReminderCard(
                            habit = habit,
                            globalEnabled = uiState.globalNotificationEnabled,
                            onToggle = { onToggleReminder(habit) }
                        )
                    }
                }
            }
        }

        if (uiState.showDeleteConfirmation && uiState.habitToDelete != null) {
            DeleteConfirmationDialog(
                habitName = uiState.habitToDelete.name,
                onConfirm = onConfirmDelete,
                onDismiss = onDismissDelete
            )
        }
    }
}

@Composable
fun GlobalNotificationBanner(
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(EmeraldDark, Emerald)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Outlined.NotificationsActive
                        else Icons.Outlined.NotificationsOff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Notifikasi Aktif",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isEnabled) "Semua pengingat aktif" else "Pengingat dinonaktifkan",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}

@Composable
fun HabitReminderCard(
    habit: Habit,
    globalEnabled: Boolean,
    onToggle: () -> Unit
) {
    val isEffectivelyEnabled = globalEnabled && habit.isReminderEnabled
    val cardAlpha by animateFloatAsState(targetValue = if (globalEnabled) 1f else 0.6f, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mosque icon in circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(EmeraldLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDD4C", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
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
                    if (habit.time.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = EmeraldLight
                        ) {
                            Text(
                                text = habit.time,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Emerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    // Frequency
                    Text(
                        text = "Setiap hari",
                        fontSize = 12.sp,
                        color = Gray500
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = habit.isReminderEnabled,
                onCheckedChange = { onToggle() },
                enabled = globalEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceWhite,
                    checkedTrackColor = Emerald,
                    uncheckedThumbColor = SurfaceWhite,
                    uncheckedTrackColor = Gray300
                )
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    habitName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Ibadah", fontWeight = FontWeight.SemiBold) },
        text = { Text("Apakah Anda yakin ingin menghapus \"$habitName\"? Ini akan menghapus data terkait.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = ErrorRed, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun EmptyNotificationState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.NotificationsOff, null, modifier = Modifier.size(48.dp), tint = Gray400)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tidak Ada Ibadah", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Semua ibadah Anda akan muncul di sini.",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray500,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    HabitIslamiTheme {
        NotificationScreenContent(
            uiState = NotificationUiState(
                isLoading = false,
                habits = listOf(
                    Habit(1, "Sholat Subuh", "Sholat Fardhu", "\uD83C\uDF05", "", false, time = "04:30"),
                    Habit(2, "Sholat Dzuhur", "Sholat Fardhu", "\u2600\uFE0F", "", false, time = "12:00"),
                    Habit(3, "Sholat Ashar", "Sholat Fardhu", "\u2601\uFE0F", "", false, time = "15:15"),
                    Habit(4, "Sholat Maghrib", "Sholat Fardhu", "\uD83C\uDF19", "", false, time = "18:00"),
                    Habit(5, "Sholat Isya", "Sholat Fardhu", "\uD83C\uDF1C", "", false, time = "19:15")
                )
            ),
            onToggleGlobalNotification = {},
            onToggleReminder = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {}
        )
    }
}
