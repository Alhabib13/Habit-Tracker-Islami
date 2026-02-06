package com.islami.Aha.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// PRIMARY COLORS - Emerald Islamic Palette
// ============================================================================
val EmeraldDark = Color(0xFF0D7C5F)       // Header, tombol utama
val Emerald = Color(0xFF14A87D)            // Aksen utama
val EmeraldLight = Color(0xFFE8F5F0)       // Background card ringan
val EmeraldMuted = Color(0xFF2ECC9B)       // Dark mode primary

// Gradient Colors for Primary
val GradientStart = Color(0xFF0D7C5F)
val GradientEnd = Color(0xFF14A87D)

// ============================================================================
// SECONDARY COLORS - Gold Islamic Accent
// ============================================================================
val Gold = Color(0xFFD4AF37)               // Streak, badge, highlight
val GoldLight = Color(0xFFFFF8E7)          // Background highlight
val GoldMuted = Color(0xFFF0C75E)          // Dark mode gold
val GoldShimmer = Color(0xFFFFD700)        // Animasi shimmer

// ============================================================================
// NEUTRAL COLORS - Blue-Black Tones
// ============================================================================
val TextPrimary = Color(0xFF1A2B3C)        // Teks utama (bukan hitam murni)
val TextSecondary = Color(0xFF6B7B8D)      // Teks sekunder
val TextTertiary = Color(0xFF8899AA)       // Teks tersier

// Gray Scale
val Gray900 = Color(0xFF1A2B3C)
val Gray800 = Color(0xFF2D3E50)
val Gray700 = Color(0xFF3D5166)
val Gray600 = Color(0xFF4E6479)
val Gray500 = Color(0xFF6B7B8D)
val Gray400 = Color(0xFF8899AA)
val Gray300 = Color(0xFFB0BEC5)
val Gray200 = Color(0xFFD6DEE3)
val Gray100 = Color(0xFFECF0F3)
val Gray50 = Color(0xFFF5F7F9)

// ============================================================================
// BACKGROUND & SURFACE COLORS
// ============================================================================
val BackgroundLight = Color(0xFFFAFBFD)    // Background utama (bukan putih murni)
val SurfaceWhite = Color(0xFFFFFFFF)       // Card surface
val SurfaceElevated = Color(0xFFFEFEFE)    // Elevated surface
val Divider = Color(0xFFF0F2F5)            // Garis pembatas

// Dark Mode Backgrounds
val BackgroundDark = Color(0xFF0F1419)
val SurfaceDark = Color(0xFF1A2332)
val CardDark = Color(0xFF1E2A3A)
val ElevatedDark = Color(0xFF243447)

// ============================================================================
// SEMANTIC COLORS
// ============================================================================
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFE74C3C)
val InfoBlue = Color(0xFF3B82F6)

// ============================================================================
// CATEGORY GRADIENT COLORS
// ============================================================================
// Sholat - Emerald
val CategorySholatStart = Color(0xFF0D7C5F)
val CategorySholatEnd = Color(0xFF14A87D)

// Dzikir - Teal
val CategoryDzikirStart = Color(0xFF0891B2)
val CategoryDzikirEnd = Color(0xFF22D3EE)

// Tilawah - Amber/Gold
val CategoryTilawahStart = Color(0xFFD97706)
val CategoryTilawahEnd = Color(0xFFFBBF24)

// Puasa - Purple/Indigo
val CategoryPuasaStart = Color(0xFF6366F1)
val CategoryPuasaEnd = Color(0xFF8B5CF6)

// ============================================================================
// DARK MODE TEXT COLORS
// ============================================================================
val TextPrimaryDark = Color(0xFFE8ECF0)
val TextSecondaryDark = Color(0xFF8899AA)
val TextTertiaryDark = Color(0xFF6B7B8D)

// ============================================================================
// SPECIAL EFFECT COLORS
// ============================================================================
val Overlay = Color(0x80000000)            // 50% black overlay
val Scrim = Color(0xB3000000)              // 70% black scrim
val Shimmer = Color(0x33FFFFFF)            // Shimmer effect
val Ripple = Color(0xFF14A87D)             // Ripple color

// ============================================================================
// LEGACY COMPATIBILITY (untuk komponen yang belum diupdate)
// ============================================================================
@Deprecated("Use Emerald instead", ReplaceWith("Emerald"))
val GreenPrimary = Emerald

@Deprecated("Use EmeraldLight instead", ReplaceWith("EmeraldLight"))
val GreenLight = EmeraldLight

@Deprecated("Use EmeraldDark instead", ReplaceWith("EmeraldDark"))
val GreenDark = EmeraldDark

@Deprecated("Use Gold instead", ReplaceWith("Gold"))
val IslamicGold = Gold
