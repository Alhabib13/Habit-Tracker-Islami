package com.islami.Aha.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islami.Aha.ui.theme.*

/**
 * Splash Screen - Layar pembuka aplikasi Aha.
 *
 * Menampilkan logo, nama aplikasi, dan tagline dengan animasi fade-in.
 * Setelah 2 detik, akan navigasi ke:
 * - Login Screen (jika belum pernah login)
 * - Home Screen (jika sudah login sebelumnya)
 *
 * @param viewModel ViewModel untuk mengelola state dan logic navigasi
 * @param onNavigateToLogin Callback navigasi ke layar Login
 * @param onNavigateToHome Callback navigasi ke layar Home
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation berdasarkan state
    LaunchedEffect(uiState.shouldNavigate) {
        if (uiState.shouldNavigate) {
            if (uiState.isUserLoggedIn) {
                onNavigateToHome()
            } else {
                onNavigateToLogin()
            }
        }
    }

    SplashScreenContent()
}

/**
 * Konten Splash Screen dengan animasi.
 *
 * Komponen ini bersifat stateless dan hanya menangani tampilan.
 * Animasi yang digunakan:
 * - Fade in untuk semua elemen
 * - Scale up untuk logo
 */
@Composable
fun SplashScreenContent() {
    // Animasi fade in
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )

    // Animasi scale untuk logo
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    // Trigger animasi saat composable pertama kali ditampilkan
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .alpha(alphaAnim.value)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Container dengan background putih rounded
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnim.value),
                shape = RoundedCornerShape(28.dp),
                color = SurfaceWhite
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Icon Masjid sebagai logo
                    Icon(
                        imageVector = MosqueIcon,
                        contentDescription = "Aha Logo",
                        tint = GreenPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nama Aplikasi
            Text(
                text = "Aha",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceWhite,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Pelacak Ibadah Islami",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = SurfaceWhite.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-tagline
            Text(
                text = "Catat, Pantau, Tingkatkan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = SurfaceWhite.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        // Version di bagian bawah
        Text(
            text = "Versi 1.0",
            fontSize = 12.sp,
            color = SurfaceWhite.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

/**
 * Icon Masjid placeholder.
 * Menggunakan Home icon karena Mosque icon tidak tersedia di default icons.
 * Untuk produksi, gunakan custom drawable atau icon dari library.
 */
private val MosqueIcon: ImageVector
    get() = Icons.Outlined.Mosque

// Extension untuk Mosque icon (sama seperti di HomeScreen)
private val Icons.Outlined.Mosque: ImageVector
    get() = Icons.Outlined.Home

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    HabitIslamiTheme {
        SplashScreenContent()
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Splash Dark")
@Composable
fun SplashScreenDarkPreview() {
    HabitIslamiTheme(darkTheme = true) {
        SplashScreenContent()
    }
}
