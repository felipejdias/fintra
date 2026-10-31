package com.fjdias.personalfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fjdias.personalfinance.Transaction
import com.fjdias.personalfinance.formatCurrency
import com.fjdias.personalfinance.ui.components.EmptyState
import com.fjdias.personalfinance.ui.theme.FinanceColors
import com.fjdias.personalfinance.ui.theme.Green20
import com.fjdias.personalfinance.ui.theme.Green30
import com.fjdias.personalfinance.ui.theme.Green40
import kotlin.math.abs

@Composable
fun SummaryScreen(transactions: List<Transaction>) {
    val income = transactions.filter { it.categoryName == "Renda" }.sumOf { abs(it.amount) }
    val expenses = transactions.filter { it.categoryName != "Renda" }.sumOf { abs(it.amount) }
    val balance = income - expenses

    if (transactions.isEmpty()) {
        EmptyState(
            icon = Icons.Default.AccountBalanceWallet,
            title = "Sem dados para resumir",
            subtitle = "Adicione transações para ver o resumo financeiro",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BalanceHeroCard(balance)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FlowStatCard(
                label = "Entradas",
                value = income,
                icon = Icons.Default.ArrowUpward,
                color = FinanceColors.income,
                containerColor = FinanceColors.incomeContainer,
                testTag = "SummaryIncome",
                modifier = Modifier.weight(1f)
            )
            FlowStatCard(
                label = "Saídas",
                value = expenses,
                icon = Icons.Default.ArrowDownward,
                color = FinanceColors.expense,
                containerColor = FinanceColors.expenseContainer,
                testTag = "SummaryExpenses",
                modifier = Modifier.weight(1f)
            )
        }

        if (income > 0) {
            CommitmentCard(expenses, income)
        }
    }
}

@Composable
private fun BalanceHeroCard(balance: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Green40, Green30, Green20)
                )
            )
            .padding(24.dp)
            .testTag("SummaryCard")
    ) {
        Column {
            Text(
                text = "Saldo do período",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "R$ ${formatCurrency(balance)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp).testTag("SummaryBalance")
            )
            Text(
                text = if (balance >= 0) "Suas finanças estão positivas" else "Atenção: saldo negativo",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun FlowStatCard(
    label: String,
    value: Double,
    icon: ImageVector,
    color: Color,
    containerColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FinanceColors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(containerColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "R$ ${formatCurrency(value)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

@Composable
private fun CommitmentCard(expenses: Double, income: Double) {
    val ratio = (expenses / income).toFloat().coerceIn(0f, 1f)
    val percent = (ratio * 100).toInt()
    val barColor = if (ratio >= 0.9f) FinanceColors.expense else MaterialTheme.colorScheme.primary

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FinanceColors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comprometimento da renda",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = "Dos seus ganhos, $percent% foram gastos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
