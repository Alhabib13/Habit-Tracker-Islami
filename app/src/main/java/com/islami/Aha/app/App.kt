package com.islami.Aha.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.islami.Aha.ui.theme.HabitIslamiTheme

/**
 * AhaApp - Root composable untuk aplikasi Aha.
 *
 * Composable ini berfungsi sebagai titik masuk UI tunggal yang:
 * 1. Membungkus seluruh aplikasi dengan HabitIslamiTheme
 * 2. Menyediakan NavController untuk navigasi
 * 3. Menghosting NavHost utama
 *
 * Penggunaan:
 * - Dipanggil dari MainActivity.setContent {}
 * - Tidak memiliki logika bisnis, hanya struktur UI
 *
 * @see AhaNavHost untuk definisi rute navigasi
 * @see HabitIslamiTheme untuk konfigurasi tema
 */
@Composable
fun AhaApp() {
    HabitIslamiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // NavController untuk mengelola navigasi antar layar
            val navController = rememberNavController()

            // Mengambil rute saat ini untuk keperluan monitoring/debugging
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route

            // NavHost utama yang mengelola semua layar
            AhaNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Daftar rute yang menampilkan Bottom Navigation.
 * Digunakan untuk menentukan kapan Bottom Navigation harus ditampilkan.
 *
 * Catatan: Saat ini Bottom Navigation dikelola di masing-masing layar
 * menggunakan AhaBottomNavBar composable untuk fleksibilitas yang lebih baik.
 */
val bottomNavRoutes = listOf(
    Screen.Home.route,
    Screen.Statistic.route,
    Screen.Notification.route,
    Screen.Profile.route
)

/**
 * Extension function untuk memeriksa apakah rute saat ini
 * memerlukan Bottom Navigation.
 *
 * @param route Rute yang akan diperiksa
 * @return true jika rute memerlukan Bottom Navigation
 */
fun shouldShowBottomNav(route: String?): Boolean {
    return route in bottomNavRoutes
}

/**
 * Daftar rute yang merupakan bagian dari Auth Flow.
 * Layar-layar ini tidak memiliki Bottom Navigation.
 */
val authRoutes = listOf(
    Screen.Splash.route,
    Screen.Login.route,
    Screen.Register.route
)

/**
 * Daftar rute yang tidak menampilkan Bottom Navigation
 * tetapi bukan bagian dari Auth Flow.
 */
val noBottomNavRoutes = listOf(
    Screen.AddHabit.route,
    Screen.Settings.route
)

/**
 * Extension function untuk memeriksa apakah rute saat ini
 * adalah bagian dari Auth Flow.
 *
 * @param route Rute yang akan diperiksa
 * @return true jika rute adalah bagian dari Auth Flow
 */
fun isAuthRoute(route: String?): Boolean {
    return route in authRoutes
}
