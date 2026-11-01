package app.fintra.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.abs

// Esquema claro — seed verde esmeralda
val Green40 = Color(0xFF006B4F)
val Green10 = Color(0xFF002117)
val Green20 = Color(0xFF003826)
val Green30 = Color(0xFF00513B)
val Green80 = Color(0xFF6FDBB0)
val Green90 = Color(0xFF8BF6CB)

val GreenGrey40 = Color(0xFF4C6358)
val GreenGrey80 = Color(0xFFB3CCBF)
val GreenGrey90 = Color(0xFFCEE9DA)
val GreenGreyDark10 = Color(0xFF082016)
val GreenGreyDark30 = Color(0xFF354B41)

val Teal40 = Color(0xFF3F6374)
val Teal80 = Color(0xFFA7CCDE)
val Teal90 = Color(0xFFC2E8FD)
val TealDark10 = Color(0xFF001F2A)
val TealDark30 = Color(0xFF274A5C)

val Neutral10 = Color(0xFF191C1A)
val Neutral90 = Color(0xFFE1E3DF)
val NeutralVariant30 = Color(0xFF404943)
val NeutralVariant50 = Color(0xFF707973)
val NeutralVariant80 = Color(0xFFBFC9C2)
val NeutralVariant90 = Color(0xFFDBE5DF)
val SurfaceLight = Color(0xFFF5FBF6)
val SurfaceDark = Color(0xFF0E1512)
val SurfaceVariantLight = Color(0xFFE7EFEA)

object FinanceColors {
    val income: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF4ADE80) else Color(0xFF0B7A48)
    val expense: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF28B82) else Color(0xFFD64545)
    val incomeContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF14532D) else Color(0xFFDCFCE7)
    val expenseContainer: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF7F1D1D) else Color(0xFFFEE2E2)
    val card: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF182019) else Color(0xFFFFFFFF)
}

val CategoryPalette = listOf(
    Color(0xFF6366F1),
    Color(0xFFF59E0B),
    Color(0xFFEC4899),
    Color(0xFF10B981),
    Color(0xFF3B82F6),
    Color(0xFF8B5CF6),
    Color(0xFFEF4444),
    Color(0xFF14B8A6),
    Color(0xFFF97316),
    Color(0xFF84CC16)
)

fun categoryColor(name: String): Color =
    if (name == "Renda") Color(0xFF0B7A48)
    else CategoryPalette[abs(name.hashCode()) % CategoryPalette.size]

fun categoryIcon(name: String): ImageVector = when {
    name == "Renda" -> Icons.Default.Payments
    name.contains("aliment", ignoreCase = true) || name.contains("food", ignoreCase = true) || name.contains("restaur", ignoreCase = true) -> Icons.Default.Restaurant
    name.contains("transport", ignoreCase = true) || name.contains("carro", ignoreCase = true) || name.contains("uber", ignoreCase = true) -> Icons.Default.DirectionsCar
    name.contains("saúde", ignoreCase = true) || name.contains("saude", ignoreCase = true) || name.contains("farm", ignoreCase = true) -> Icons.Default.LocalHospital
    name.contains("moradia", ignoreCase = true) || name.contains("casa", ignoreCase = true) || name.contains("aluguel", ignoreCase = true) -> Icons.Default.Home
    name.contains("educa", ignoreCase = true) || name.contains("curso", ignoreCase = true) -> Icons.Default.School
    name.contains("compra", ignoreCase = true) || name.contains("mercado", ignoreCase = true) -> Icons.Default.ShoppingCart
    name.contains("lazer", ignoreCase = true) || name.contains("divers", ignoreCase = true) || name.contains("jogo", ignoreCase = true) -> Icons.Default.SportsEsports
    name.contains("trabalho", ignoreCase = true) || name.contains("salário", ignoreCase = true) -> Icons.Default.Work
    name.contains("academia", ignoreCase = true) || name.contains("fitness", ignoreCase = true) -> Icons.Default.FitnessCenter
    name.contains("cartão", ignoreCase = true) || name.contains("cartao", ignoreCase = true) || name.contains("assinatura", ignoreCase = true) -> Icons.Default.CreditCard
    else -> Icons.Default.Category
}
