package com.islami.Aha.ui.home

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
 * Unit tests untuk HomeViewModel.
 *
 * Menguji:
 * - Initial state yang benar
 * - Toggle prayer completion
 * - Toggle prayer notification
 * - Toggle quick access items
 * - Prayer tab selection
 * - Format countdown function
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========================================================================
    // INITIAL STATE TESTS
    // ========================================================================

    @Test
    fun `initial state has loading true`() = runTest {
        // Given: ViewModel baru dibuat
        val newViewModel = HomeViewModel()

        // Then: isLoading harus true pada awalnya
        assertTrue(newViewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has default location Jakarta`() = runTest {
        // Given: ViewModel baru dibuat
        val newViewModel = HomeViewModel()

        // Then: Location default adalah Jakarta
        assertEquals("Jakarta", newViewModel.uiState.value.location)
    }

    @Test
    fun `initial state has fardhu tab selected`() = runTest {
        // Given: ViewModel sudah di-init
        advanceUntilIdle()

        // Then: Tab default adalah fardhu
        assertEquals("fardhu", viewModel.uiState.value.selectedPrayerTab)
    }

    @Test
    fun `initial state loads prayer times after init`() = runTest {
        // Given: ViewModel sudah di-init
        advanceUntilIdle()

        // Then: Prayer times harus terisi dengan 5 sholat fardhu
        assertEquals(5, viewModel.uiState.value.prayerTimes.size)
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Subuh" })
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Dzuhur" })
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Ashar" })
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Maghrib" })
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Isya" })
    }

    @Test
    fun `initial state sets loading to false after data loaded`() = runTest {
        // Given: ViewModel sudah di-init dan data dimuat
        advanceUntilIdle()

        // Then: isLoading harus false setelah data dimuat
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial quick access items are not completed`() = runTest {
        // Given: ViewModel sudah di-init
        advanceUntilIdle()

        // Then: Semua quick access items belum selesai
        assertFalse(viewModel.uiState.value.sholatCompleted)
        assertFalse(viewModel.uiState.value.dzikirCompleted)
        assertFalse(viewModel.uiState.value.tilawahCompleted)
        assertFalse(viewModel.uiState.value.puasaCompleted)
    }

    // ========================================================================
    // PRAYER TAB SELECTION TESTS
    // ========================================================================

    @Test
    fun `onPrayerTabSelected changes to sunnah tab`() = runTest {
        // Given: ViewModel sudah di-init dengan tab fardhu
        advanceUntilIdle()
        assertEquals("fardhu", viewModel.uiState.value.selectedPrayerTab)

        // When: Memilih tab sunnah
        viewModel.onPrayerTabSelected("sunnah")
        advanceUntilIdle()

        // Then: Tab berubah ke sunnah dan prayer times diupdate
        assertEquals("sunnah", viewModel.uiState.value.selectedPrayerTab)
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Dhuha" })
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Tahajud" })
    }

    @Test
    fun `onPrayerTabSelected back to fardhu tab`() = runTest {
        // Given: ViewModel di tab sunnah
        advanceUntilIdle()
        viewModel.onPrayerTabSelected("sunnah")
        advanceUntilIdle()

        // When: Kembali ke tab fardhu
        viewModel.onPrayerTabSelected("fardhu")
        advanceUntilIdle()

        // Then: Tab berubah ke fardhu dan prayer times diupdate
        assertEquals("fardhu", viewModel.uiState.value.selectedPrayerTab)
        assertEquals(5, viewModel.uiState.value.prayerTimes.size)
        assertTrue(viewModel.uiState.value.prayerTimes.any { it.name == "Subuh" })
    }

    // ========================================================================
    // TOGGLE PRAYER COMPLETION TESTS
    // ========================================================================

    @Test
    fun `togglePrayerCompletion marks prayer as completed`() = runTest {
        // Given: ViewModel sudah di-init dengan sholat belum selesai
        advanceUntilIdle()
        val subuhBefore = viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }
        assertFalse(subuhBefore!!.isCompleted)

        // When: Toggle completion untuk Subuh
        viewModel.togglePrayerCompletion("Subuh")
        advanceUntilIdle()

        // Then: Subuh ditandai selesai
        val subuhAfter = viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }
        assertTrue(subuhAfter!!.isCompleted)
    }

    @Test
    fun `togglePrayerCompletion unmarks completed prayer`() = runTest {
        // Given: Subuh sudah selesai
        advanceUntilIdle()
        viewModel.togglePrayerCompletion("Subuh")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }!!.isCompleted)

        // When: Toggle lagi untuk Subuh
        viewModel.togglePrayerCompletion("Subuh")
        advanceUntilIdle()

        // Then: Subuh tidak lagi ditandai selesai
        assertFalse(viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }!!.isCompleted)
    }

    @Test
    fun `togglePrayerCompletion updates completed count`() = runTest {
        // Given: ViewModel sudah di-init dengan count 0
        advanceUntilIdle()
        assertEquals(0, viewModel.uiState.value.completedHabitsCount)

        // When: Toggle completion untuk Subuh dan Dzuhur
        viewModel.togglePrayerCompletion("Subuh")
        advanceUntilIdle()
        viewModel.togglePrayerCompletion("Dzuhur")
        advanceUntilIdle()

        // Then: Count bertambah menjadi 2
        assertEquals(2, viewModel.uiState.value.completedHabitsCount)
    }

    // ========================================================================
    // TOGGLE PRAYER NOTIFICATION TESTS
    // ========================================================================

    @Test
    fun `togglePrayerNotification disables notification`() = runTest {
        // Given: Subuh notification enabled
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }!!.notificationEnabled)

        // When: Toggle notification untuk Subuh
        viewModel.togglePrayerNotification("Subuh")
        advanceUntilIdle()

        // Then: Notification dinonaktifkan
        assertFalse(viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }!!.notificationEnabled)
    }

    @Test
    fun `togglePrayerNotification enables notification`() = runTest {
        // Given: Subuh notification disabled
        advanceUntilIdle()
        viewModel.togglePrayerNotification("Subuh")
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }!!.notificationEnabled)

        // When: Toggle lagi untuk Subuh
        viewModel.togglePrayerNotification("Subuh")
        advanceUntilIdle()

        // Then: Notification diaktifkan kembali
        assertTrue(viewModel.uiState.value.prayerTimes.find { it.name == "Subuh" }!!.notificationEnabled)
    }

    // ========================================================================
    // TOGGLE QUICK ACCESS TESTS
    // ========================================================================

    @Test
    fun `toggleQuickAccessCompletion marks sholat as completed`() = runTest {
        // Given: Sholat belum selesai
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.sholatCompleted)

        // When: Toggle sholat
        viewModel.toggleQuickAccessCompletion("sholat")
        advanceUntilIdle()

        // Then: Sholat ditandai selesai
        assertTrue(viewModel.uiState.value.sholatCompleted)
    }

    @Test
    fun `toggleQuickAccessCompletion marks dzikir as completed`() = runTest {
        // Given: Dzikir belum selesai
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.dzikirCompleted)

        // When: Toggle dzikir
        viewModel.toggleQuickAccessCompletion("dzikir")
        advanceUntilIdle()

        // Then: Dzikir ditandai selesai
        assertTrue(viewModel.uiState.value.dzikirCompleted)
    }

    @Test
    fun `toggleQuickAccessCompletion marks tilawah as completed`() = runTest {
        // Given: Tilawah belum selesai
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.tilawahCompleted)

        // When: Toggle tilawah
        viewModel.toggleQuickAccessCompletion("tilawah")
        advanceUntilIdle()

        // Then: Tilawah ditandai selesai
        assertTrue(viewModel.uiState.value.tilawahCompleted)
    }

    @Test
    fun `toggleQuickAccessCompletion marks puasa as completed`() = runTest {
        // Given: Puasa belum selesai
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.puasaCompleted)

        // When: Toggle puasa
        viewModel.toggleQuickAccessCompletion("puasa")
        advanceUntilIdle()

        // Then: Puasa ditandai selesai
        assertTrue(viewModel.uiState.value.puasaCompleted)
    }

    @Test
    fun `toggleQuickAccessCompletion ignores unknown type`() = runTest {
        // Given: ViewModel sudah di-init
        advanceUntilIdle()
        val stateBefore = viewModel.uiState.value

        // When: Toggle dengan type tidak dikenal
        viewModel.toggleQuickAccessCompletion("unknown")
        advanceUntilIdle()

        // Then: State tidak berubah
        assertEquals(stateBefore.sholatCompleted, viewModel.uiState.value.sholatCompleted)
        assertEquals(stateBefore.dzikirCompleted, viewModel.uiState.value.dzikirCompleted)
    }

    // ========================================================================
    // FORMAT COUNTDOWN TESTS
    // ========================================================================

    @Test
    fun `formatCountdown returns correct format for hours and minutes`() {
        // Given: 90 menit (1 jam 30 menit)
        val minutes = 90

        // When: Format countdown
        val result = viewModel.formatCountdown(minutes)

        // Then: Format benar
        assertEquals("1 jam 30 menit lagi", result)
    }

    @Test
    fun `formatCountdown returns correct format for full hours`() {
        // Given: 120 menit (2 jam)
        val minutes = 120

        // When: Format countdown
        val result = viewModel.formatCountdown(minutes)

        // Then: Format benar tanpa menit
        assertEquals("2 jam lagi", result)
    }

    @Test
    fun `formatCountdown returns correct format for minutes only`() {
        // Given: 45 menit
        val minutes = 45

        // When: Format countdown
        val result = viewModel.formatCountdown(minutes)

        // Then: Format benar
        assertEquals("45 menit lagi", result)
    }

    @Test
    fun `formatCountdown returns waktu sholat for zero minutes`() {
        // Given: 0 menit
        val minutes = 0

        // When: Format countdown
        val result = viewModel.formatCountdown(minutes)

        // Then: Return waktu sholat
        assertEquals("Waktu sholat", result)
    }
}
