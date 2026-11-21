package app.viajejapon.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// Paleta tradicional japonesa
val Washi = Color(0xFFF3ECDD)      // papel washi
val Paper = Color(0xFFFBF8F0)      // papel claro
val Sumi = Color(0xFF1F1B17)       // tinta sumi
val Shu = Color(0xFFC63A2B)        // bermellón (shu-iro)
val ShuSoft = Color(0xFFF2D9D2)
val Ai = Color(0xFF2B4666)         // índigo (ai-iro)
val AiSoft = Color(0xFFDCE3EA)
val Matcha = Color(0xFF7A8B52)
val Kin = Color(0xFFC79A33)        // dorado
val Nezu = Color(0xFF857B6F)       // gris cálido
val Line = Color(0xFFDCD2BE)

private fun Typography.withFont(f: FontFamily) = Typography(
    displayLarge = displayLarge.copy(fontFamily = f),
    displayMedium = displayMedium.copy(fontFamily = f),
    displaySmall = displaySmall.copy(fontFamily = f),
    headlineLarge = headlineLarge.copy(fontFamily = f),
    headlineMedium = headlineMedium.copy(fontFamily = f),
    headlineSmall = headlineSmall.copy(fontFamily = f),
    titleLarge = titleLarge.copy(fontFamily = f),
    titleMedium = titleMedium.copy(fontFamily = f),
    titleSmall = titleSmall.copy(fontFamily = f),
    bodyLarge = bodyLarge.copy(fontFamily = f),
    bodyMedium = bodyMedium.copy(fontFamily = f),
    bodySmall = bodySmall.copy(fontFamily = f),
    labelLarge = labelLarge.copy(fontFamily = f),
    labelMedium = labelMedium.copy(fontFamily = f),
    labelSmall = labelSmall.copy(fontFamily = f),
)

@Composable
fun JapanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Shu,
            onPrimary = Paper,
            primaryContainer = ShuSoft,
            onPrimaryContainer = Sumi,
            secondary = Ai,
            onSecondary = Paper,
            secondaryContainer = AiSoft,
            onSecondaryContainer = Sumi,
            tertiary = Matcha,
            background = Washi,
            onBackground = Sumi,
            surface = Paper,
            onSurface = Sumi,
            surfaceVariant = Color(0xFFEBE2CF),
            onSurfaceVariant = Nezu,
            outline = Nezu,
            outlineVariant = Line,
            error = Shu,
            surfaceContainerLowest = Paper,
            surfaceContainerLow = Paper,
            surfaceContainer = Paper,
            surfaceContainerHigh = Paper,
            surfaceContainerHighest = Color(0xFFEBE2CF),
        ),
        typography = Typography().withFont(FontFamily.Serif),
        content = content,
    )
}
