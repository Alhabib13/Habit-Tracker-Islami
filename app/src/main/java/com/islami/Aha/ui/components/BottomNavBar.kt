package com.islami.Aha.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islami.Aha.ui.theme.*

/**
 * Data class yang merepresentasikan item dalam Bottom Navigation.
 *
 * @property route Rute navigasi yang terkait dengan item ini
 * @property title Label yang ditampilkan di bawah ikon
 * @property icon Ikon yang ditampilkan untuk item ini
 * @property contentDescription Deskripsi untuk aksesibilitas
 */
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val contentDescription: String = title
)

/**
 * Daftar item untuk Bottom Navigation.
 * Urutan: Beranda, Statistik, Tambah, Notifikasi, Profil
 */
object BottomNavItems {
    val items = listOf(
        BottomNavItem(
            route = "home",
            title = "Beranda",
            icon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = "statistic",
            title = "Statistik",
            icon = Icons.Outlined.BarChart
        ),
        BottomNavItem(
            route = "add_habit",
            title = "Tambah",
            icon = Icons.Default.Add
        ),
        BottomNavItem(
            route = "notification",
            title = "Notifikasi",
            icon = Icons.Outlined.Notifications
        ),
        BottomNavItem(
            route = "profile",
            title = "Profil",
            icon = Icons.Outlined.Person
        )
    )
}

/**
 * Komponen Bottom Navigation Bar yang dapat digunakan kembali.
 * Mengikuti design system aplikasi dengan warna tema.
 *
 * @param currentRoute Rute layar yang sedang aktif
 * @param onItemSelected Callback ketika item dipilih, mengembalikan route
 * @param modifier Modifier untuk kustomisasi
 */
@Composable
fun AhaBottomNavBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 0.dp,
        modifier = modifier
            .height(70.dp)
            .shadow(
                elevation = 8.dp,
                spotColor = Color.Black.copy(alpha = 0.08f),
                ambientColor = Color.Black.copy(alpha = 0.04f)
            )
    ) {
        BottomNavItems.items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = isSelected,
                onClick = { onItemSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Gray400,
                    unselectedTextColor = Gray400
                )
            )
        }
    }
}

// ============================================================================
// PREVIEW
// ============================================================================

@Preview(showBackground = true)
@Composable
fun AhaBottomNavBarPreview() {
    HabitIslamiTheme {
        AhaBottomNavBar(
            currentRoute = "home",
            onItemSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AhaBottomNavBarStatisticSelectedPreview() {
    HabitIslamiTheme {
        AhaBottomNavBar(
            currentRoute = "statistic",
            onItemSelected = {}
        )
    }
}
