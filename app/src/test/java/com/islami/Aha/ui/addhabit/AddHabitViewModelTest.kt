package com.islami.Aha.ui.addhabit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests untuk AddHabitViewModel.
 *
 * Menguji:
 * - Initial state (default values)
 * - Input validation
 * - Save habit flow
 * - State updates
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddHabitViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AddHabitViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddHabitViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========================================================================
    // INITIAL STATE TESTS
    // ========================================================================

    @Test
    fun `initial state has empty name`() {
        assertEquals("", viewModel.uiState.value.name)
    }

    @Test
    fun `initial state has empty description`() {
        assertEquals("", viewModel.uiState.value.description)
    }

    @Test
    fun `initial state has default category SHOLAT_FARDHU`() {
        assertEquals(HabitCategory.SHOLAT_FARDHU, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `initial state has default frequency DAILY`() {
        assertEquals(HabitFrequency.DAILY, viewModel.uiState.value.selectedFrequency)
    }

    @Test
    fun `initial state has target count 1`() {
        assertEquals(1, viewModel.uiState.value.targetCount)
    }

    @Test
    fun `initial state has reminder enabled`() {
        assertTrue(viewModel.uiState.value.isReminderEnabled)
    }

    @Test
    fun `initial state has default reminder time 05 00`() {
        assertEquals(5, viewModel.uiState.value.reminderHour)
        assertEquals(0, viewModel.uiState.value.reminderMinute)
    }

    @Test
    fun `initial state has all days selected`() {
        val selectedDays = viewModel.uiState.value.selectedDays
        assertEquals(7, selectedDays.size)
        assertTrue(selectedDays.all { it.isSelected })
    }

    @Test
    fun `initial state is not loading`() {
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has no name error`() {
        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun `initial state has save success false`() {
        assertFalse(viewModel.uiState.value.saveSuccess)
    }

    // ========================================================================
    // INPUT HANDLERS TESTS
    // ========================================================================

    @Test
    fun `onNameChange updates name`() {
        // When: Mengubah nama
        viewModel.onNameChange("Sholat Tahajud")

        // Then: Nama diupdate
        assertEquals("Sholat Tahajud", viewModel.uiState.value.name)
    }

    @Test
    fun `onNameChange clears existing error`() {
        // Given: Ada error pada nama
        viewModel.saveHabit() // Trigger error karena nama kosong
        assertNotNull(viewModel.uiState.value.nameError)

        // When: Mengubah nama
        viewModel.onNameChange("New Name")

        // Then: Error dihapus
        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun `onDescriptionChange updates description`() {
        // When: Mengubah deskripsi
        viewModel.onDescriptionChange("Deskripsi kebiasaan baru")

        // Then: Deskripsi diupdate
        assertEquals("Deskripsi kebiasaan baru", viewModel.uiState.value.description)
    }

    @Test
    fun `onCategorySelected updates category`() {
        // When: Memilih kategori
        viewModel.onCategorySelected(HabitCategory.DZIKIR)

        // Then: Kategori diupdate dan dropdown ditutup
        assertEquals(HabitCategory.DZIKIR, viewModel.uiState.value.selectedCategory)
        assertFalse(viewModel.uiState.value.isCategoryExpanded)
    }

    @Test
    fun `toggleCategoryDropdown toggles expanded state`() {
        // Given: Dropdown tertutup
        assertFalse(viewModel.uiState.value.isCategoryExpanded)

        // When: Toggle dropdown
        viewModel.toggleCategoryDropdown()

        // Then: Dropdown terbuka
        assertTrue(viewModel.uiState.value.isCategoryExpanded)

        // When: Toggle lagi
        viewModel.toggleCategoryDropdown()

        // Then: Dropdown tertutup
        assertFalse(viewModel.uiState.value.isCategoryExpanded)
    }

    @Test
    fun `dismissCategoryDropdown closes dropdown`() {
        // Given: Dropdown terbuka
        viewModel.toggleCategoryDropdown()
        assertTrue(viewModel.uiState.value.isCategoryExpanded)

        // When: Dismiss dropdown
        viewModel.dismissCategoryDropdown()

        // Then: Dropdown tertutup
        assertFalse(viewModel.uiState.value.isCategoryExpanded)
    }

    @Test
    fun `onFrequencySelected updates frequency`() {
        // When: Memilih frekuensi
        viewModel.onFrequencySelected(HabitFrequency.WEEKLY)

        // Then: Frekuensi diupdate dan dropdown ditutup
        assertEquals(HabitFrequency.WEEKLY, viewModel.uiState.value.selectedFrequency)
        assertFalse(viewModel.uiState.value.isFrequencyExpanded)
    }

    @Test
    fun `toggleFrequencyDropdown toggles expanded state`() {
        // Given: Dropdown tertutup
        assertFalse(viewModel.uiState.value.isFrequencyExpanded)

        // When: Toggle dropdown
        viewModel.toggleFrequencyDropdown()

        // Then: Dropdown terbuka
        assertTrue(viewModel.uiState.value.isFrequencyExpanded)
    }

    // ========================================================================
    // TARGET COUNT TESTS
    // ========================================================================

    @Test
    fun `incrementTargetCount increases count`() {
        // Given: Count awal adalah 1
        assertEquals(1, viewModel.uiState.value.targetCount)

        // When: Increment
        viewModel.incrementTargetCount()

        // Then: Count bertambah
        assertEquals(2, viewModel.uiState.value.targetCount)
    }

    @Test
    fun `incrementTargetCount does not exceed 99`() {
        // Given: Set count ke 99
        repeat(98) { viewModel.incrementTargetCount() }
        assertEquals(99, viewModel.uiState.value.targetCount)

        // When: Increment lagi
        viewModel.incrementTargetCount()

        // Then: Count tetap 99
        assertEquals(99, viewModel.uiState.value.targetCount)
    }

    @Test
    fun `decrementTargetCount decreases count`() {
        // Given: Count adalah 5
        repeat(4) { viewModel.incrementTargetCount() }
        assertEquals(5, viewModel.uiState.value.targetCount)

        // When: Decrement
        viewModel.decrementTargetCount()

        // Then: Count berkurang
        assertEquals(4, viewModel.uiState.value.targetCount)
    }

    @Test
    fun `decrementTargetCount does not go below 1`() {
        // Given: Count adalah 1
        assertEquals(1, viewModel.uiState.value.targetCount)

        // When: Decrement
        viewModel.decrementTargetCount()

        // Then: Count tetap 1
        assertEquals(1, viewModel.uiState.value.targetCount)
    }

    @Test
    fun `onTargetCountChange updates count within valid range`() {
        // When: Set count ke 50
        viewModel.onTargetCountChange(50)

        // Then: Count diupdate
        assertEquals(50, viewModel.uiState.value.targetCount)
    }

    @Test
    fun `onTargetCountChange ignores invalid values`() {
        // Given: Count awal adalah 1
        assertEquals(1, viewModel.uiState.value.targetCount)

        // When: Set count ke 0
        viewModel.onTargetCountChange(0)

        // Then: Count tidak berubah
        assertEquals(1, viewModel.uiState.value.targetCount)

        // When: Set count ke 100
        viewModel.onTargetCountChange(100)

        // Then: Count tidak berubah
        assertEquals(1, viewModel.uiState.value.targetCount)
    }

    // ========================================================================
    // REMINDER TESTS
    // ========================================================================

    @Test
    fun `toggleReminder disables reminder`() {
        // Given: Reminder enabled
        assertTrue(viewModel.uiState.value.isReminderEnabled)

        // When: Toggle
        viewModel.toggleReminder()

        // Then: Reminder disabled
        assertFalse(viewModel.uiState.value.isReminderEnabled)
    }

    @Test
    fun `toggleReminder enables reminder`() {
        // Given: Reminder disabled
        viewModel.toggleReminder()
        assertFalse(viewModel.uiState.value.isReminderEnabled)

        // When: Toggle lagi
        viewModel.toggleReminder()

        // Then: Reminder enabled
        assertTrue(viewModel.uiState.value.isReminderEnabled)
    }

    @Test
    fun `onReminderTimeChange updates time`() {
        // When: Mengubah waktu
        viewModel.onReminderTimeChange(14, 30)

        // Then: Waktu diupdate dan time picker ditutup
        assertEquals(14, viewModel.uiState.value.reminderHour)
        assertEquals(30, viewModel.uiState.value.reminderMinute)
        assertFalse(viewModel.uiState.value.showTimePicker)
    }

    @Test
    fun `showTimePicker shows time picker`() {
        // When: Show time picker
        viewModel.showTimePicker()

        // Then: Time picker ditampilkan
        assertTrue(viewModel.uiState.value.showTimePicker)
    }

    @Test
    fun `hideTimePicker hides time picker`() {
        // Given: Time picker terbuka
        viewModel.showTimePicker()
        assertTrue(viewModel.uiState.value.showTimePicker)

        // When: Hide time picker
        viewModel.hideTimePicker()

        // Then: Time picker disembunyikan
        assertFalse(viewModel.uiState.value.showTimePicker)
    }

    // ========================================================================
    // DAY SELECTION TESTS
    // ========================================================================

    @Test
    fun `toggleDaySelection deselects day`() {
        // Given: Senin terpilih
        val mondayBefore = viewModel.uiState.value.selectedDays.find { it.id == 1 }
        assertTrue(mondayBefore!!.isSelected)

        // When: Toggle Senin (id = 1)
        viewModel.toggleDaySelection(1)

        // Then: Senin tidak terpilih
        val mondayAfter = viewModel.uiState.value.selectedDays.find { it.id == 1 }
        assertFalse(mondayAfter!!.isSelected)
    }

    @Test
    fun `toggleDaySelection selects day`() {
        // Given: Senin tidak terpilih
        viewModel.toggleDaySelection(1)
        assertFalse(viewModel.uiState.value.selectedDays.find { it.id == 1 }!!.isSelected)

        // When: Toggle Senin lagi
        viewModel.toggleDaySelection(1)

        // Then: Senin terpilih kembali
        assertTrue(viewModel.uiState.value.selectedDays.find { it.id == 1 }!!.isSelected)
    }

    // ========================================================================
    // VALIDATION TESTS
    // ========================================================================

    @Test
    fun `saveHabit shows error for empty name`() {
        // Given: Nama kosong
        assertEquals("", viewModel.uiState.value.name)

        // When: Save
        viewModel.saveHabit()

        // Then: Error ditampilkan
        assertEquals("Nama kebiasaan tidak boleh kosong", viewModel.uiState.value.nameError)
    }

    @Test
    fun `saveHabit shows error for name less than 3 characters`() {
        // Given: Nama terlalu pendek
        viewModel.onNameChange("Ab")

        // When: Save
        viewModel.saveHabit()

        // Then: Error ditampilkan
        assertEquals("Nama minimal 3 karakter", viewModel.uiState.value.nameError)
    }

    @Test
    fun `saveHabit shows error for name more than 50 characters`() {
        // Given: Nama terlalu panjang
        viewModel.onNameChange("A".repeat(51))

        // When: Save
        viewModel.saveHabit()

        // Then: Error ditampilkan
        assertEquals("Nama maksimal 50 karakter", viewModel.uiState.value.nameError)
    }

    @Test
    fun `saveHabit shows error for blank name with spaces`() {
        // Given: Nama hanya spasi
        viewModel.onNameChange("   ")

        // When: Save
        viewModel.saveHabit()

        // Then: Error ditampilkan
        assertEquals("Nama kebiasaan tidak boleh kosong", viewModel.uiState.value.nameError)
    }

    // ========================================================================
    // SAVE FLOW TESTS
    // ========================================================================

    @Test
    fun `saveHabit with valid name sets loading state`() = runTest {
        // Given: Nama valid
        viewModel.onNameChange("Sholat Tahajud")

        // When: Save
        viewModel.saveHabit()

        // Then: Loading state true (sebelum coroutine selesai)
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `saveHabit with valid name completes successfully`() = runTest {
        // Given: Nama valid
        viewModel.onNameChange("Sholat Tahajud")

        // When: Save dan tunggu selesai
        viewModel.saveHabit()
        advanceUntilIdle()

        // Then: Save berhasil
        assertTrue(viewModel.uiState.value.saveSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // ========================================================================
    // RESET STATE TESTS
    // ========================================================================

    @Test
    fun `resetState returns to initial values`() = runTest {
        // Given: State sudah diubah
        viewModel.onNameChange("Sholat Tahajud")
        viewModel.onDescriptionChange("Deskripsi")
        viewModel.onCategorySelected(HabitCategory.DZIKIR)
        viewModel.incrementTargetCount()

        // When: Reset state
        viewModel.resetState()

        // Then: Kembali ke nilai awal
        assertEquals("", viewModel.uiState.value.name)
        assertEquals("", viewModel.uiState.value.description)
        assertEquals(HabitCategory.SHOLAT_FARDHU, viewModel.uiState.value.selectedCategory)
        assertEquals(1, viewModel.uiState.value.targetCount)
    }

    // ========================================================================
    // UTILITY FUNCTION TESTS
    // ========================================================================

    @Test
    fun `getFormattedReminderTime returns correct format`() {
        // Given: Waktu default 05:00
        assertEquals(5, viewModel.uiState.value.reminderHour)
        assertEquals(0, viewModel.uiState.value.reminderMinute)

        // When: Get formatted time
        val result = viewModel.getFormattedReminderTime()

        // Then: Format benar
        assertEquals("05:00", result)
    }

    @Test
    fun `getFormattedReminderTime with different time`() {
        // Given: Ubah waktu ke 14:30
        viewModel.onReminderTimeChange(14, 30)

        // When: Get formatted time
        val result = viewModel.getFormattedReminderTime()

        // Then: Format benar
        assertEquals("14:30", result)
    }

    @Test
    fun `getFrequencyDescription returns Setiap hari for DAILY`() {
        // Given: Frekuensi DAILY
        assertEquals(HabitFrequency.DAILY, viewModel.uiState.value.selectedFrequency)

        // When: Get description
        val result = viewModel.getFrequencyDescription()

        // Then: Deskripsi benar
        assertEquals("Setiap hari", result)
    }

    @Test
    fun `getFrequencyDescription returns Setiap bulan for MONTHLY`() {
        // Given: Frekuensi MONTHLY
        viewModel.onFrequencySelected(HabitFrequency.MONTHLY)

        // When: Get description
        val result = viewModel.getFrequencyDescription()

        // Then: Deskripsi benar
        assertEquals("Setiap bulan", result)
    }

    @Test
    fun `getFrequencyDescription returns selected days for WEEKLY`() {
        // Given: Frekuensi WEEKLY dengan beberapa hari dipilih
        viewModel.onFrequencySelected(HabitFrequency.WEEKLY)
        viewModel.toggleDaySelection(3) // Deselect Rabu
        viewModel.toggleDaySelection(5) // Deselect Jumat
        viewModel.toggleDaySelection(7) // Deselect Minggu

        // When: Get description
        val result = viewModel.getFrequencyDescription()

        // Then: Deskripsi berisi hari yang dipilih
        assertTrue(result.contains("Sen"))
        assertTrue(result.contains("Sel"))
        assertFalse(result.contains("Rab"))
    }
}
