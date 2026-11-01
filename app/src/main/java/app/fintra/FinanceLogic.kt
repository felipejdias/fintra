package app.fintra

import kotlin.math.abs

data class FinanceSummary(
    val income: Double,
    val expenses: Double,
    val balance: Double,
    val commitment: Float
)

object FinanceLogic {

    // Regra de negócio: só "Renda" conta como entrada; tudo o resto é saída,
    // independente do sinal do valor (usa-se o módulo).
    fun summarize(transactions: List<Transaction>): FinanceSummary {
        val income = transactions.filter { it.categoryName == "Renda" }.sumOf { abs(it.amount) }
        val expenses = transactions.filter { it.categoryName != "Renda" }.sumOf { abs(it.amount) }
        val commitment = if (income > 0) (expenses / income).toFloat().coerceIn(0f, 1f) else 0f
        return FinanceSummary(income, expenses, income - expenses, commitment)
    }

    fun matchesFilters(
        transaction: Transaction,
        query: String,
        year: Int?,
        month: Int?,
        category: String?
    ): Boolean {
        val matchesSearch = transaction.title.contains(query, ignoreCase = true)
        val matchesYear = year == null || transaction.date.year == year
        val matchesMonth = month == null || transaction.date.monthValue == month
        val matchesCategory = category == null || transaction.categoryName == category
        return matchesSearch && matchesYear && matchesMonth && matchesCategory
    }

    fun matchesAnalyticsPeriod(transaction: Transaction, year: Int?, month: Int?): Boolean {
        val matchesYear = year == null || transaction.date.year == year
        val matchesMonth = month == null || transaction.date.monthValue == month
        return matchesYear && matchesMonth
    }
}
