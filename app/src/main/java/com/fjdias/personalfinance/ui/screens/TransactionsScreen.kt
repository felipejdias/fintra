package com.fjdias.personalfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fjdias.personalfinance.Category
import com.fjdias.personalfinance.FinanceViewModel
import com.fjdias.personalfinance.Transaction
import com.fjdias.personalfinance.formatCurrency
import com.fjdias.personalfinance.ui.components.EmptyState
import com.fjdias.personalfinance.ui.components.TransactionDialog
import com.fjdias.personalfinance.ui.theme.FinanceColors
import com.fjdias.personalfinance.ui.theme.categoryColor
import com.fjdias.personalfinance.ui.theme.categoryIcon
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Composable
fun TransactionsListScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    viewModel: FinanceViewModel
) {
    if (transactions.isEmpty()) {
        EmptyState(
            icon = Icons.Default.ReceiptLong,
            title = "Nenhuma transação",
            subtitle = "Toque em + para adicionar ou importe um CSV no menu superior",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val grouped = transactions.groupBy { it.date }.toSortedMap(compareByDescending { it })

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("TransactionsList"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "count_header") {
            Text(
                text = "${transactions.size} transações",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        grouped.forEach { (date, dayTransactions) ->
            item(key = "header_$date") {
                DateGroupHeader(date)
            }
            items(dayTransactions, key = { it.id }) { transaction ->
                TransactionItem(transaction, categories, viewModel)
            }
        }
    }
}

@Composable
private fun DateGroupHeader(date: LocalDate) {
    val today = LocalDate.now()
    val label = when (date) {
        today -> "Hoje"
        today.minusDays(1) -> "Ontem"
        else -> date.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", java.util.Locale("pt", "BR")))
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun TransactionItem(transaction: Transaction, categories: List<Category>, viewModel: FinanceViewModel) {
    var showEditDialog by remember { mutableStateOf(false) }
    if (showEditDialog) {
        TransactionDialog(
            title = "Editar Transação",
            initialTransaction = transaction,
            categories = categories,
            onDismiss = { showEditDialog = false },
            onConfirm = { t, a, c, d ->
                viewModel.updateTransaction(transaction.copy(title = t, amount = a, categoryName = c, date = d))
                showEditDialog = false
            }
        )
    }

    val isIncome = transaction.categoryName == "Renda"
    val accent = categoryColor(transaction.categoryName)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FinanceColors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true }
            .testTag("TransactionItem_${transaction.title}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accent.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(transaction.categoryName),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("TransactionCategoryName_${transaction.title}")
                )
            }
            Text(
                text = "${if (isIncome) "+" else "-"} R$ ${formatCurrency(abs(transaction.amount))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) FinanceColors.income else FinanceColors.expense,
                modifier = Modifier.testTag("TransactionAmount_${transaction.title}")
            )
        }
    }
}
