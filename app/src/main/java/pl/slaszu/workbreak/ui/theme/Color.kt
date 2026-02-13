package pl.slaszu.workbreak.ui.theme

import androidx.compose.ui.graphics.Color

// Główne kolory bazowe wyciągnięte z ikony
val WorkbreakBlue = Color(0xFF4F63F7) // Główny, żywy niebieski z ikony
val WorkbreakLightBlue = Color(0xFFE0E5FF) // Bardzo jasny niebieski (do tła kontenerów)
val WorkbreakDarkBlue = Color(0xFF00164F) // Ciemny niebieski (do tekstu na jasnym tle)

// Pozostałe kolory
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val NeutralGrayLight = Color(0xFFFBFBFF) // Prawie białe tło z lekkim niebieskim zabarwieniem
val NeutralGrayDark = Color(0xFF1A1C1E)  // Ciemne tło

// --- Schemat kolorów dla trybu JASNEGO (Light Theme) ---
val md_theme_light_primary = WorkbreakBlue
val md_theme_light_onPrimary = White
val md_theme_light_primaryContainer = WorkbreakLightBlue
val md_theme_light_onPrimaryContainer = WorkbreakDarkBlue

// Używamy tego samego niebieskiego jako koloru "secondary" dla spójności,
// ale możesz tu użyć innego koloru dopełniającego.
val md_theme_light_secondary = WorkbreakBlue
val md_theme_light_onSecondary = White
val md_theme_light_secondaryContainer = Color(0xFFDDE1FF)
val md_theme_light_onSecondaryContainer = Color(0xFF001A4F)

val md_theme_light_background = NeutralGrayLight
val md_theme_light_onBackground = NeutralGrayDark
val md_theme_light_surface = NeutralGrayLight
val md_theme_light_onSurface = NeutralGrayDark

// --- Schemat kolorów dla trybu CIEMNEGO (Dark Theme) ---
// W trybie ciemnym kolory są bardziej stonowane i pastelowe, aby nie męczyć oczu.
val md_theme_dark_primary = Color(0xFFBFC6FF) // Rozjaśniona wersja głównego niebieskiego
val md_theme_dark_onPrimary = Color(0xFF1C2E8D)
val md_theme_dark_primaryContainer = Color(0xFF3547C0)
val md_theme_dark_onPrimaryContainer = Color(0xFFE0E5FF)

val md_theme_dark_secondary = Color(0xFFBFC6FF)
val md_theme_dark_onSecondary = Color(0xFF1C2E8D)
val md_theme_dark_secondaryContainer = Color(0xFF3547C0)
val md_theme_dark_onSecondaryContainer = Color(0xFFE0E5FF)

val md_theme_dark_background = NeutralGrayDark
val md_theme_dark_onBackground = Color(0xFFE3E2E6)
val md_theme_dark_surface = NeutralGrayDark
val md_theme_dark_onSurface = Color(0xFFE3E2E6)