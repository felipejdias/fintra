package com.fjdias.personalfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fjdias.personalfinance.Category
import com.fjdias.personalfinance.Transaction
import java.util.Locale

@Composable
private fun FilterChipRow(
    testTag: String,
    content: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag(testTag),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) { content() }
}

@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        modifier = Modifier.testTag(testTag)
    )
}

@Composable
fun YearFilter(selectedYear: Int?, onYearSelected: (Int?) -> Unit, transactions: List<Transaction>) {
    val years = transactions.map { it.date.year }.distinct().sortedDescending()
    FilterChipRow("YearFilter") {
        item {
            FilterChipItem(
                label = "Todos Anos",
                selected = selectedYear == null,
                testTag = "YearTab_All",
                onClick = { onYearSelected(null) }
            )
        }
        items(years) { year ->
            FilterChipItem(
                label = year.toString(),
                selected = selectedYear == year,
                testTag = "YearTab_$year",
                onClick = { onYearSelected(year) }
            )
        }
    }
}

@Composable
fun MonthFilter(
    selectedMonth: Int?,
    selectedYear: Int?,
    onMonthSelected: (Int?) -> Unit,
    transactions: List<Transaction>
) {
    val filteredTransactionsForMonth = if (selectedYear != null) {
        transactions.filter { it.date.year == selectedYear }
    } else {
        transactions
    }
    val months = filteredTransactionsForMonth.map { it.date.monthValue }.distinct().sorted()
    val locale = Locale.forLanguageTag("pt-BR")
    FilterChipRow("MonthFilter") {
        item {
            FilterChipItem(
                label = "Todos Meses",
                selected = selectedMonth == null,
                testTag = "MonthTab_All",
                onClick = { onMonthSelected(null) }
            )
        }
        items(months) { month ->
            val monthName = java.time.Month.of(month)
                .getDisplayName(java.time.format.TextStyle.SHORT, locale)
                .replaceFirstChar { it.uppercase() }
            FilterChipItem(
                label = monthName,
                selected = selectedMonth == month,
                testTag = "MonthTab_$month",
                onClick = { onMonthSelected(month) }
            )
        }
    }
}

@Composable
fun CategoryFilter(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    categories: List<Category>
) {
    FilterChipRow("CategoryFilter") {
        item {
            FilterChipItem(
                label = "Todas Categorias",
                selected = selectedCategory == null,
                testTag = "CategoryTab_All",
                onClick = { onCategorySelected(null) }
            )
        }
        items(categories) { category ->
            FilterChipItem(
                label = category.name,
                selected = selectedCategory == category.name,
                testTag = "CategoryTab_${category.name}",
                onClick = { onCategorySelected(category.name) }
            )
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("SearchBar"),
        placeholder = { Text("Buscar transação...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
