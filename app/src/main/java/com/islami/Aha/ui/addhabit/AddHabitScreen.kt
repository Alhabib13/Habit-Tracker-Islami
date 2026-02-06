package com.islami.Aha.ui.addhabit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islami.Aha.ui.theme.*

/**
 * Add Habit Screen - Layar untuk menambah kebiasaan ibadah baru.
 *
 * Fitur:
 * - Input nama kebiasaan
 * - Input deskripsi (opsional)
 * - Pilih kategori ibadah
 * - Pilih frekuensi (harian/mingguan/bulanan/kustom)
 * - Set target jumlah
 * - Set waktu pengingat
 *
 * @param viewModel ViewModel untuk mengelola state form
 * @param onNavigateBack Callback untuk kembali ke layar sebelumnya
 * @param onHabitSaved Callback ketika habit berhasil disimpan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    viewModel: AddHabitViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onHabitSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Handle save success
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onHabitSaved()
            viewModel.resetState()
        }
    }

    AddHabitScreenContent(
        uiState = uiState,
        formattedReminderTime = viewModel.getFormattedReminderTime(),
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategorySelected = viewModel::onCategorySelected,
        onToggleCategoryDropdown = viewModel::toggleCategoryDropdown,
        onDismissCategoryDropdown = viewModel::dismissCategoryDropdown,
        onFrequencySelected = viewModel::onFrequencySelected,
        onToggleFrequencyDropdown = viewModel::toggleFrequencyDropdown,
        onDismissFrequencyDropdown = viewModel::dismissFrequencyDropdown,
        onIncrementTarget = viewModel::incrementTargetCount,
        onDecrementTarget = viewModel::decrementTargetCount,
        onToggleReminder = viewModel::toggleReminder,
        onShowTimePicker = viewModel::showTimePicker,
        onHideTimePicker = viewModel::hideTimePicker,
        onReminderTimeChange = viewModel::onReminderTimeChange,
        onToggleDaySelection = viewModel::toggleDaySelection,
        onSaveClick = {
            focusManager.clearFocus()
            viewModel.saveHabit()
        },
        onNavigateBack = onNavigateBack
    )
}

/**
 * Konten Add Habit Screen - Stateless composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreenContent(
    uiState: AddHabitUiState,
    formattedReminderTime: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategorySelected: (HabitCategory) -> Unit,
    onToggleCategoryDropdown: () -> Unit,
    onDismissCategoryDropdown: () -> Unit,
    onFrequencySelected: (HabitFrequency) -> Unit,
    onToggleFrequencyDropdown: () -> Unit,
    onDismissFrequencyDropdown: () -> Unit,
    onIncrementTarget: () -> Unit,
    onDecrementTarget: () -> Unit,
    onToggleReminder: () -> Unit,
    onShowTimePicker: () -> Unit,
    onHideTimePicker: () -> Unit,
    onReminderTimeChange: (Int, Int) -> Unit,
    onToggleDaySelection: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Time Picker State
    val timePickerState = rememberTimePickerState(
        initialHour = uiState.reminderHour,
        initialMinute = uiState.reminderMinute,
        is24Hour = true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Kebiasaan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
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
            // Save Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceWhite,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        disabledContainerColor = GreenPrimary.copy(alpha = 0.5f)
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = SurfaceWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simpan Kebiasaan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ================================================================
            // NAMA KEBIASAAN
            // ================================================================
            FormSection(title = "Nama Kebiasaan") {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Contoh: Sholat Subuh Berjamaah",
                            color = Gray400
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = if (uiState.nameError != null) ErrorRed else Gray400
                        )
                    },
                    singleLine = true,
                    isError = uiState.nameError != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Gray200,
                        errorBorderColor = ErrorRed,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        errorContainerColor = ErrorRed.copy(alpha = 0.05f)
                    )
                )

                if (uiState.nameError != null) {
                    Text(
                        text = uiState.nameError,
                        fontSize = 12.sp,
                        color = ErrorRed,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }

            // ================================================================
            // DESKRIPSI (OPSIONAL)
            // ================================================================
            FormSection(title = "Deskripsi (Opsional)") {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = {
                        Text(
                            text = "Tambahkan catatan atau motivasi...",
                            color = Gray400
                        )
                    },
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Gray200,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    )
                )
            }

            // ================================================================
            // KATEGORI
            // ================================================================
            FormSection(title = "Kategori") {
                ExposedDropdownMenuBox(
                    expanded = uiState.isCategoryExpanded,
                    onExpandedChange = { onToggleCategoryDropdown() }
                ) {
                    OutlinedTextField(
                        value = "${uiState.selectedCategory.icon} ${uiState.selectedCategory.displayName}",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isCategoryExpanded)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Gray200,
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = uiState.isCategoryExpanded,
                        onDismissRequest = onDismissCategoryDropdown
                    ) {
                        HabitCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${category.icon} ${category.displayName}",
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = { onCategorySelected(category) },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            // ================================================================
            // FREKUENSI
            // ================================================================
            FormSection(title = "Frekuensi") {
                ExposedDropdownMenuBox(
                    expanded = uiState.isFrequencyExpanded,
                    onExpandedChange = { onToggleFrequencyDropdown() }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedFrequency.displayName,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Gray500
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isFrequencyExpanded)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Gray200,
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = uiState.isFrequencyExpanded,
                        onDismissRequest = onDismissFrequencyDropdown
                    ) {
                        HabitFrequency.entries.forEach { frequency ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = frequency.displayName,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = { onFrequencySelected(frequency) },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                // Day selector (untuk Weekly dan Custom)
                AnimatedVisibility(
                    visible = uiState.selectedFrequency == HabitFrequency.WEEKLY ||
                            uiState.selectedFrequency == HabitFrequency.CUSTOM
                ) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = "Pilih Hari",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gray700,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            uiState.selectedDays.forEach { day ->
                                DayChip(
                                    day = day,
                                    onClick = { onToggleDaySelection(day.id) }
                                )
                            }
                        }
                    }
                }
            }

            // ================================================================
            // TARGET
            // ================================================================
            FormSection(title = "Target Harian") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jumlah target per hari",
                            fontSize = 14.sp,
                            color = Gray700
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Decrement button
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(enabled = uiState.targetCount > 1) {
                                        onDecrementTarget()
                                    },
                                color = if (uiState.targetCount > 1) GreenPrimary.copy(alpha = 0.1f)
                                else Gray100,
                                shape = CircleShape
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Kurangi",
                                        tint = if (uiState.targetCount > 1) GreenPrimary else Gray400,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Count display
                            Text(
                                text = "${uiState.targetCount}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gray900,
                                modifier = Modifier.width(40.dp),
                                textAlign = TextAlign.Center
                            )

                            // Increment button
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(enabled = uiState.targetCount < 99) {
                                        onIncrementTarget()
                                    },
                                color = if (uiState.targetCount < 99) GreenPrimary.copy(alpha = 0.1f)
                                else Gray100,
                                shape = CircleShape
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah",
                                        tint = if (uiState.targetCount < 99) GreenPrimary else Gray400,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ================================================================
            // PENGINGAT
            // ================================================================
            FormSection(title = "Pengingat") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Toggle pengingat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = GreenPrimary
                                )
                                Text(
                                    text = "Aktifkan Pengingat",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Gray700
                                )
                            }

                            Switch(
                                checked = uiState.isReminderEnabled,
                                onCheckedChange = { onToggleReminder() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SurfaceWhite,
                                    checkedTrackColor = GreenPrimary,
                                    uncheckedThumbColor = SurfaceWhite,
                                    uncheckedTrackColor = Gray300
                                )
                            )
                        }

                        // Time picker (jika pengingat aktif)
                        AnimatedVisibility(visible = uiState.isReminderEnabled) {
                            HorizontalDivider(color = Gray100)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onShowTimePicker() }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = Gray500
                                    )
                                    Text(
                                        text = "Waktu Pengingat",
                                        fontSize = 14.sp,
                                        color = Gray700
                                    )
                                }

                                Text(
                                    text = formattedReminderTime,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GreenPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Time Picker Dialog
    if (uiState.showTimePicker) {
        TimePickerDialog(
            onDismiss = onHideTimePicker,
            onConfirm = {
                onReminderTimeChange(timePickerState.hour, timePickerState.minute)
            }
        ) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = GreenPrimary.copy(alpha = 0.1f),
                    selectorColor = GreenPrimary,
                    containerColor = SurfaceWhite,
                    periodSelectorBorderColor = GreenPrimary,
                    periodSelectorSelectedContainerColor = GreenPrimary,
                    periodSelectorSelectedContentColor = SurfaceWhite,
                    timeSelectorSelectedContainerColor = GreenPrimary,
                    timeSelectorSelectedContentColor = SurfaceWhite
                )
            )
        }
    }
}

/**
 * Section wrapper untuk form dengan judul.
 */
@Composable
fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Gray700,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

/**
 * Chip untuk memilih hari.
 */
@Composable
fun DayChip(
    day: DayOfWeek,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        color = if (day.isSelected) GreenPrimary else SurfaceWhite,
        shape = CircleShape,
        border = if (!day.isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, Gray200)
        } else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.shortName.take(1),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (day.isSelected) SurfaceWhite else Gray600
            )
        }
    }
}

/**
 * Dialog untuk Time Picker.
 */
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "OK",
                    color = GreenPrimary,
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
        title = {
            Text(
                text = "Pilih Waktu",
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        },
        text = { content() },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(20.dp)
    )
}

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddHabitScreenPreview() {
    HabitIslamiTheme {
        AddHabitScreenContent(
            uiState = AddHabitUiState(
                name = "",
                description = "",
                selectedCategory = HabitCategory.SHOLAT_FARDHU,
                selectedFrequency = HabitFrequency.DAILY,
                targetCount = 5,
                isReminderEnabled = true,
                reminderHour = 5,
                reminderMinute = 0
            ),
            formattedReminderTime = "05:00",
            onNameChange = {},
            onDescriptionChange = {},
            onCategorySelected = {},
            onToggleCategoryDropdown = {},
            onDismissCategoryDropdown = {},
            onFrequencySelected = {},
            onToggleFrequencyDropdown = {},
            onDismissFrequencyDropdown = {},
            onIncrementTarget = {},
            onDecrementTarget = {},
            onToggleReminder = {},
            onShowTimePicker = {},
            onHideTimePicker = {},
            onReminderTimeChange = { _, _ -> },
            onToggleDaySelection = {},
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Add Habit with Weekly")
@Composable
fun AddHabitScreenWeeklyPreview() {
    HabitIslamiTheme {
        AddHabitScreenContent(
            uiState = AddHabitUiState(
                name = "Sholat Tahajud",
                description = "Bangun malam untuk sholat tahajud",
                selectedCategory = HabitCategory.TAHAJUD,
                selectedFrequency = HabitFrequency.WEEKLY,
                targetCount = 1,
                isReminderEnabled = true,
                reminderHour = 3,
                reminderMinute = 30,
                selectedDays = listOf(
                    DayOfWeek(1, "Sen", "Senin", true),
                    DayOfWeek(2, "Sel", "Selasa", false),
                    DayOfWeek(3, "Rab", "Rabu", true),
                    DayOfWeek(4, "Kam", "Kamis", false),
                    DayOfWeek(5, "Jum", "Jumat", true),
                    DayOfWeek(6, "Sab", "Sabtu", false),
                    DayOfWeek(7, "Min", "Minggu", true)
                )
            ),
            formattedReminderTime = "03:30",
            onNameChange = {},
            onDescriptionChange = {},
            onCategorySelected = {},
            onToggleCategoryDropdown = {},
            onDismissCategoryDropdown = {},
            onFrequencySelected = {},
            onToggleFrequencyDropdown = {},
            onDismissFrequencyDropdown = {},
            onIncrementTarget = {},
            onDecrementTarget = {},
            onToggleReminder = {},
            onShowTimePicker = {},
            onHideTimePicker = {},
            onReminderTimeChange = { _, _ -> },
            onToggleDaySelection = {},
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Add Habit with Error")
@Composable
fun AddHabitScreenErrorPreview() {
    HabitIslamiTheme {
        AddHabitScreenContent(
            uiState = AddHabitUiState(
                name = "Ab",
                nameError = "Nama minimal 3 karakter"
            ),
            formattedReminderTime = "05:00",
            onNameChange = {},
            onDescriptionChange = {},
            onCategorySelected = {},
            onToggleCategoryDropdown = {},
            onDismissCategoryDropdown = {},
            onFrequencySelected = {},
            onToggleFrequencyDropdown = {},
            onDismissFrequencyDropdown = {},
            onIncrementTarget = {},
            onDecrementTarget = {},
            onToggleReminder = {},
            onShowTimePicker = {},
            onHideTimePicker = {},
            onReminderTimeChange = { _, _ -> },
            onToggleDaySelection = {},
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Add Habit Loading")
@Composable
fun AddHabitScreenLoadingPreview() {
    HabitIslamiTheme {
        AddHabitScreenContent(
            uiState = AddHabitUiState(
                name = "Sholat Subuh Berjamaah",
                isLoading = true
            ),
            formattedReminderTime = "05:00",
            onNameChange = {},
            onDescriptionChange = {},
            onCategorySelected = {},
            onToggleCategoryDropdown = {},
            onDismissCategoryDropdown = {},
            onFrequencySelected = {},
            onToggleFrequencyDropdown = {},
            onDismissFrequencyDropdown = {},
            onIncrementTarget = {},
            onDecrementTarget = {},
            onToggleReminder = {},
            onShowTimePicker = {},
            onHideTimePicker = {},
            onReminderTimeChange = { _, _ -> },
            onToggleDaySelection = {},
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}
