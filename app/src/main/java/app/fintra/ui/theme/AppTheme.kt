package app.fintra.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils

val LocalAppTheme = compositionLocalOf { AppTheme.ESMERALDA }

enum class AppTheme(
    val label: String,
    private val primarySeed: Int,
    private val secondarySeed: Int,
    private val tertiarySeed: Int
) {
    ESMERALDA("Esmeralda", 0xFF006B4F.toInt(), 0xFF4C6358.toInt(), 0xFF3F6374.toInt()),
    AMETISTA("Ametista", 0xFF6750A4.toInt(), 0xFF625B71.toInt(), 0xFF7D5260.toInt()),
    OCEANO("Oceano", 0xFF0061A4.toInt(), 0xFF4F6178.toInt(), 0xFF1E6A87.toInt()),
    AMBAR("Âmbar", 0xFF7F5800.toInt(), 0xFF6E5D40.toInt(), 0xFF566632.toInt()),
    POR_DO_SOL("Pôr do Sol", 0xFF9A4525.toInt(), 0xFF775654.toInt(), 0xFF705D00.toInt()),
    AURORA("Aurora", 0xFF00696B.toInt(), 0xFF7A5AAA.toInt(), 0xFFA04073.toInt());

    val previewColors = listOf(Color(primarySeed), Color(secondarySeed), Color(tertiarySeed))

    val heroGradient = listOf(
        Color(tone(primarySeed, 40)),
        Color(tone(primarySeed, 30)),
        Color(tone(primarySeed, 20))
    )

    fun lightScheme(): ColorScheme = lightColorScheme(
        primary = Color(tone(primarySeed, 40)), onPrimary = Color(tone(primarySeed, 100)),
        primaryContainer = Color(tone(primarySeed, 90)), onPrimaryContainer = Color(tone(primarySeed, 10)),
        secondary = Color(tone(secondarySeed, 40)), onSecondary = Color(tone(secondarySeed, 100)),
        secondaryContainer = Color(tone(secondarySeed, 90)), onSecondaryContainer = Color(tone(secondarySeed, 10)),
        tertiary = Color(tone(tertiarySeed, 40)), onTertiary = Color(tone(tertiarySeed, 100)),
        tertiaryContainer = Color(tone(tertiarySeed, 90)), onTertiaryContainer = Color(tone(tertiarySeed, 10)),
        background = Color(neutral(98)), onBackground = Color(neutral(10)),
        surface = Color(neutral(98)), onSurface = Color(neutral(10)),
        surfaceVariant = Color(neutralVariant(90)), onSurfaceVariant = Color(neutralVariant(30)),
        outline = Color(neutralVariant(50)), outlineVariant = Color(neutralVariant(80)),
        surfaceContainerLowest = Color(neutral(100)),
        surfaceContainerLow = Color(neutral(96)),
        surfaceContainer = Color(neutral(94)),
        surfaceContainerHigh = Color(neutral(92)),
        surfaceContainerHighest = Color(neutral(90))
    )

    fun darkScheme(): ColorScheme = darkColorScheme(
        primary = Color(tone(primarySeed, 80)), onPrimary = Color(tone(primarySeed, 20)),
        primaryContainer = Color(tone(primarySeed, 30)), onPrimaryContainer = Color(tone(primarySeed, 90)),
        secondary = Color(tone(secondarySeed, 80)), onSecondary = Color(tone(secondarySeed, 20)),
        secondaryContainer = Color(tone(secondarySeed, 30)), onSecondaryContainer = Color(tone(secondarySeed, 90)),
        tertiary = Color(tone(tertiarySeed, 80)), onTertiary = Color(tone(tertiarySeed, 20)),
        tertiaryContainer = Color(tone(tertiarySeed, 30)), onTertiaryContainer = Color(tone(tertiarySeed, 90)),
        background = Color(neutral(6)), onBackground = Color(neutral(90)),
        surface = Color(neutral(6)), onSurface = Color(neutral(90)),
        surfaceVariant = Color(neutralVariant(30)), onSurfaceVariant = Color(neutralVariant(80)),
        outline = Color(neutralVariant(60)), outlineVariant = Color(neutralVariant(30)),
        surfaceContainerLowest = Color(neutral(4)),
        surfaceContainerLow = Color(neutral(10)),
        surfaceContainer = Color(neutral(12)),
        surfaceContainerHigh = Color(neutral(17)),
        surfaceContainerHighest = Color(neutral(22))
    )

    // Aproxima o tonal do Material: mantém hue/sat da seed e varia só a luminosidade.
    private fun tone(seed: Int, t: Int): Int = shift(seed, null, t)
    private fun neutral(t: Int): Int = shift(primarySeed, 0.06f, t)
    private fun neutralVariant(t: Int): Int = shift(primarySeed, 0.14f, t)

    private fun shift(seed: Int, sat: Float?, t: Int): Int {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(seed, hsl)
        if (sat != null) hsl[1] = sat
        hsl[2] = t.coerceIn(0, 100) / 100f
        return ColorUtils.HSLToColor(hsl)
    }

    companion object {
        fun fromKey(key: String?): AppTheme = entries.find { it.name == key } ?: ESMERALDA
    }
}
