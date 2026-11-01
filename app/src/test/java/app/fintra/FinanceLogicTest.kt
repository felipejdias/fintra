package app.fintra

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FinanceLogicTest {

    private fun tx(amount: Double, category: String, date: LocalDate = LocalDate.of(2024, 3, 15), title: String = "t") =
        Transaction(title = title, amount = amount, categoryName = category, date = date)

    @Test
    fun `summarize counts only Renda as income`() {
        val summary = FinanceLogic.summarize(listOf(
            tx(1000.0, "Renda"),
            tx(200.0, "Alimentação"),
            tx(50.0, "Lazer")
        ))
        assertEquals(1000.0, summary.income, 0.001)
        assertEquals(250.0, summary.expenses, 0.001)
        assertEquals(750.0, summary.balance, 0.001)
    }

    @Test
    fun `summarize treats negative expense amounts as expense via abs`() {
        // CSVs bancários exportam saída como negativo — não pode virar "entrada"
        val summary = FinanceLogic.summarize(listOf(
            tx(1000.0, "Renda"),
            tx(-300.0, "Alimentação")
        ))
        assertEquals(1000.0, summary.income, 0.001)
        assertEquals(300.0, summary.expenses, 0.001)
        assertEquals(700.0, summary.balance, 0.001)
    }

    @Test
    fun `summarize uses abs on income too`() {
        val summary = FinanceLogic.summarize(listOf(tx(-500.0, "Renda")))
        assertEquals(500.0, summary.income, 0.001)
        assertEquals(500.0, summary.balance, 0.001)
    }

    @Test
    fun `commitment is expenses over income clamped at 1`() {
        val over = FinanceLogic.summarize(listOf(tx(100.0, "Renda"), tx(150.0, "Outros")))
        assertEquals(1.0f, over.commitment, 0.001f)

        val under = FinanceLogic.summarize(listOf(tx(1000.0, "Renda"), tx(250.0, "Outros")))
        assertEquals(0.25f, under.commitment, 0.001f)
    }

    @Test
    fun `commitment is zero without income`() {
        val summary = FinanceLogic.summarize(listOf(tx(100.0, "Outros")))
        assertEquals(0f, summary.commitment, 0.001f)
    }

    @Test
    fun `matchesFilters applies all filters together`() {
        val t = tx(10.0, "Lazer", title = "Cinema")

        assertTrue(FinanceLogic.matchesFilters(t, "cin", 2024, 3, "Lazer"))
        assertFalse(FinanceLogic.matchesFilters(t, "praia", 2024, 3, "Lazer"))
        assertFalse(FinanceLogic.matchesFilters(t, "cin", 2023, 3, "Lazer"))
        assertFalse(FinanceLogic.matchesFilters(t, "cin", 2024, 4, "Lazer"))
        assertFalse(FinanceLogic.matchesFilters(t, "cin", 2024, 3, "Outros"))
    }

    @Test
    fun `matchesFilters ignores null filters`() {
        val t = tx(10.0, "Lazer")
        assertTrue(FinanceLogic.matchesFilters(t, "", null, null, null))
    }

    @Test
    fun `matchesAnalyticsPeriod ignores category and query`() {
        val t = tx(10.0, "Lazer")
        assertTrue(FinanceLogic.matchesAnalyticsPeriod(t, 2024, 3))
        assertFalse(FinanceLogic.matchesAnalyticsPeriod(t, 2024, 4))
        assertTrue(FinanceLogic.matchesAnalyticsPeriod(t, null, null))
    }
}
