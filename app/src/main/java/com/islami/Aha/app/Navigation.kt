package com.islami.Aha.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.islami.Aha.ui.addhabit.AddHabitScreen
import com.islami.Aha.ui.auth.LoginScreen
import com.islami.Aha.ui.auth.RegisterScreen
import com.islami.Aha.ui.home.HomeScreen
import com.islami.Aha.ui.notification.NotificationScreen
import com.islami.Aha.ui.profile.ProfileScreen
import com.islami.Aha.ui.settings.SettingsScreen
import com.islami.Aha.ui.splash.SplashScreen
import com.islami.Aha.ui.statistic.StatisticScreen

/**
 * Sealed class yang mendefinisikan semua rute navigasi dalam aplikasi.
 * Menggunakan sealed class untuk type-safety dan kemudahan maintenance.
 *
 * @property route String unik yang mengidentifikasi setiap destinasi
 */
sealed class Screen(val route: String) {
    // Auth Flow
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main Flow (dengan Bottom Navigation)
    object Home : Screen("home")
    object Statistic : Screen("statistic")
    object AddHabit : Screen("add_habit")
    object Notification : Screen("notification")
    object Profile : Screen("profile")

    // Settings (tanpa Bottom Navigation)
    object Settings : Screen("settings")
}

/**
 * NavHost utama aplikasi yang mengatur semua navigasi antar layar.
 *
 * @param navController Controller untuk mengelola navigasi
 * @param startDestination Layar awal yang ditampilkan (default: Splash)
 * @param modifier Modifier untuk NavHost
 */
@Composable
fun AhaNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ================================================================
        // AUTH FLOW
        // ================================================================

        // Splash Screen - Layar pembuka aplikasi
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        // Hapus Splash dari back stack agar user tidak bisa kembali
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // Hapus semua layar auth dari back stack
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSkipLogin = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ================================================================
        // MAIN FLOW (dengan Bottom Navigation)
        // ================================================================

        // Home Screen - Layar utama (TIDAK DIUBAH)
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddHabit = {
                    navController.navigate(Screen.AddHabit.route)
                },
                onNavigateToNotification = {
                    navController.navigate(Screen.Notification.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistic.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Statistic Screen
        composable(Screen.Statistic.route) {
            StatisticScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToAddHabit = {
                    navController.navigate(Screen.AddHabit.route)
                },
                onNavigateToNotification = {
                    navController.navigate(Screen.Notification.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Add Habit Screen
        composable(Screen.AddHabit.route) {
            AddHabitScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onHabitSaved = {
                    navController.popBackStack()
                }
            )
        }

        // Notification Screen
        composable(Screen.Notification.route) {
            NotificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToStatistic = {
                    navController.navigate(Screen.Statistic.route)
                },
                onNavigateToAddHabit = {
                    navController.navigate(Screen.AddHabit.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToStatistic = {
                    navController.navigate(Screen.Statistic.route)
                },
                onNavigateToAddHabit = {
                    navController.navigate(Screen.AddHabit.route)
                },
                onNavigateToNotification = {
                    navController.navigate(Screen.Notification.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ================================================================
        // SETTINGS (tanpa Bottom Navigation)
        // ================================================================

        // Settings Screen - Layar pengaturan aplikasi
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
