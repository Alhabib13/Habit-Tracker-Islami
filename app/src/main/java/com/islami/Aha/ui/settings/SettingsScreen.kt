 package com.islami.Aha.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islami.Aha.ui.theme.*

/**
 * Settings Screen - Layar pengaturan aplikasi.
 *
 * Layar ini menyediakan antarmuka untuk mengubah pengaturan aplikasi:
 * 1. Notifikasi - Mengaktifkan/menonaktifkan pengingat ibadah
 * 2. Mode Gelap - Mengubah tema aplikasi
 * 3. Format Waktu - Memilih format 12 jam atau 24 jam
 *
 * Implementasi mengikuti pola MVVM dengan:
 * - SettingsViewModel untuk mengelola state
 * - Stateless SettingsScreenContent untuk UI
 *
 * Catatan untuk Tesis:
 * Layar ini diimplementasikan untuk kelengkapan fungsional aplikasi.
 * Pengaturan disimpan di memori (volatile) dan tidak persisten.
 *
 * @param viewModel ViewModel untuk mengelola state pengaturan
 * @param onNavigateBack Callback untuk kembali ke layar sebelumnya
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreenContent(
        uiState = uiState,
        onToggleNotification = viewModel::toggleNotification,
        onToggleDarkMode = viewModel::toggleDarkMode,
        onShowTimeFormatDialog = viewModel::showTimeFormatDialog,
        onHideTimeFormatDialog = viewModel::hideTimeFormatDialog,
        onSetTimeFormat = viewModel::setTimeFormat,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Konten Settings Screen - Stateless composable.
 *
 * Composable ini menampilkan UI pengaturan tanpa menyimpan state internal.
 * Semua state dikelola oleh SettingsViewModel dan diteruskan sebagai parameter.
 *
 * @param uiState State UI saat ini dari ViewModel
 * @param onToggleNotification Callback saat toggle notifikasi diklik
 * @param onToggleDarkMode Callback saat toggle mode gelap diklik
 * @param onShowTimeFormatDialog Callback untuk menampilkan dialog format waktu
 * @param onHideTimeFormatDialog Callback untuk menyembunyikan dialog format waktu
 * @param onSetTimeFormat Callback saat format waktu dipilih
 * @param onNavigateBack Callback untuk navigasi kembali
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onToggleNotification: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onShowTimeFormatDialog: () -> Unit,
    onHideTimeFormatDialog: () -> Unit,
    onSetTimeFormat: (TimeFormatOption) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan",
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
        containerColor = BackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ================================================================
            // SECTION: UMUM
            // ================================================================
            item {
                SettingsSectionHeader(title = "Umum")
            }

            // Toggle Notifikasi
            item {
                SettingsToggleItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifikasi",
                    subtitle = if (uiState.notificationEnabled)
                        "Pengingat ibadah aktif"
                    else
                        "Pengingat ibadah nonaktif",
                    isChecked = uiState.notificationEnabled,
                    onToggle = onToggleNotification
                )
            }

            // Toggle Mode Gelap
            item {
                SettingsToggleItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Mode Gelap",
                    subtitle = if (uiState.darkModeEnabled)
                        "Tema gelap aktif"
                    else
                        "Tema terang aktif",
                    isChecked = uiState.darkModeEnabled,
                    onToggle = onToggleDarkMode
                )
            }

            // ================================================================
            // SECTION: TAMPILAN
            // ================================================================
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader(title = "Tampilan")
            }

            // Format Waktu
            item {
                SettingsClickableItem(
                    icon = Icons.Outlined.Schedule,
                    title = "Format Waktu",
                    subtitle = uiState.selectedTimeFormat.description,
                    value = uiState.selectedTimeFormat.displayName,
                    onClick = onShowTimeFormatDialog
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialog pemilihan format waktu
    if (uiState.showTimeFormatDialog) {
        TimeFormatSelectionDialog(
            currentFormat = uiState.selectedTimeFormat,
            onSelect = onSetTimeFormat,
            onDismiss = onHideTimeFormatDialog
        )
    }
}

/**
 * Header untuk section pengaturan.
 *
 * @param title Judul section
 */
@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Gray500,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

/**
 * Item pengaturan dengan toggle switch.
 *
 * Komponen reusable untuk menampilkan pengaturan yang dapat
 * diaktifkan/dinonaktifkan dengan switch.
 *
 * @param icon Icon yang ditampilkan di sebelah kiri
 * @param title Judul pengaturan
 * @param subtitle Deskripsi atau status pengaturan
 * @param isChecked Status switch saat ini
 * @param onToggle Callback saat switch diklik
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
            // Icon container
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

            // Text content
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
 * Item pengaturan yang dapat diklik untuk menampilkan dialog/opsi.
 *
 * Komponen reusable untuk pengaturan yang membuka dialog
 * atau navigasi ke layar lain saat diklik.
 *
 * @param icon Icon yang ditampilkan di sebelah kiri
 * @param title Judul pengaturan
 * @param subtitle Deskripsi pengaturan
 * @param value Nilai saat ini yang ditampilkan di sebelah kanan
 * @param onClick Callback saat item diklik
 */
@Composable
fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit
) {
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
            // Icon container
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

            // Text content
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

            // Value and chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = GreenPrimary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Dialog untuk memilih format waktu.
 *
 * Menampilkan pilihan format waktu (12 jam atau 24 jam)
 * dengan radio button.
 *
 * @param currentFormat Format waktu yang saat ini dipilih
 * @param onSelect Callback saat format waktu dipilih
 * @param onDismiss Callback saat dialog ditutup
 */
@Composable
fun TimeFormatSelectionDialog(
    currentFormat: TimeFormatOption,
    onSelect: (TimeFormatOption) -> Unit,
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
                TimeFormatOption.entries.forEach { format ->
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
                        Column {
                            Text(
                                text = format.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Gray900
                            )
                            Text(
                                text = format.description,
                                fontSize = 12.sp,
                                color = Gray500
                            )
                        }
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

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    HabitIslamiTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(
                notificationEnabled = true,
                darkModeEnabled = false,
                selectedTimeFormat = TimeFormatOption.HOUR_24
            ),
            onToggleNotification = {},
            onToggleDarkMode = {},
            onShowTimeFormatDialog = {},
            onHideTimeFormatDialog = {},
            onSetTimeFormat = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Settings Dark Mode On")
@Composable
fun SettingsScreenDarkModePreview() {
    HabitIslamiTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(
                notificationEnabled = false,
                darkModeEnabled = true,
                selectedTimeFormat = TimeFormatOption.HOUR_12
            ),
            onToggleNotification = {},
            onToggleDarkMode = {},
            onShowTimeFormatDialog = {},
            onHideTimeFormatDialog = {},
            onSetTimeFormat = {},
            onNavigateBack = {}
        )
    }
}
