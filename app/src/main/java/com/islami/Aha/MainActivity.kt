package com.islami.Aha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.islami.Aha.app.AhaApp

/**
 * MainActivity - Entry point aplikasi Aha.
 *
 * Activity ini berfungsi sebagai container minimal yang hanya:
 * 1. Mengaktifkan edge-to-edge display
 * 2. Memanggil AhaApp() sebagai root composable
 *
 * Semua logika UI dan navigasi dikelola di dalam AhaApp composable,
 * menjaga MainActivity tetap bersih dan sesuai prinsip single responsibility.
 *
 * Alur aplikasi:
 * 1. Splash Screen (2 detik) - animasi pembuka
 * 2. Login Screen (atau langsung ke Home jika sudah login)
 * 3. Main Flow dengan Bottom Navigation (Home, Statistik, Notifikasi, Profil)
 *
 * @see AhaApp untuk implementasi root composable
 * @see com.islami.Aha.app.AhaNavHost untuk definisi navigasi
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengaktifkan tampilan edge-to-edge untuk pengalaman immersive
        enableEdgeToEdge()

        // Memanggil root composable aplikasi
        setContent {
            AhaApp()
        }
    }
}
