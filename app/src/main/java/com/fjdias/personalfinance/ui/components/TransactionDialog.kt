package com.fjdias.personalfinance.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fjdias.personalfinance.Category
import com.fjdias.personalfinance.Transaction
import com.fjdias.personalfinance.ui.theme.FinanceColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
    title: String,
    initialTransaction: Transaction? = null,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, LocalDate) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var titleText by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var amountText by remember { mutableStateOf(initialTransaction?.amount?.let { abs(it).toString() } ?: "") }
    var selectedCategory by remember { mutableStateOf(initialTransaction?.categoryName ?: "Outros") }
    var selectedDate by remember { mutableStateOf(initialTransaction?.date ?: LocalDate.now().withDayOfMonth(1)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    if (showDatePicker) {
        MonthYearPickerDialog(
            initialYear = selectedDate.year,
            initialMonth = selectedDate.monthValue,
            onDismiss = { showDatePicker = false },
            onConfirm = { y, m ->
                selectedDate = LocalDate.of(y, m, 1)
                showDatePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Título") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("TransactionTitleField")
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor (R$)") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("TransactionAmountField")
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth().testTag("DateSelectorButton")
                ) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Data: ${selectedDate.format(DateTimeFormatter.ofPattern("MM/yyyy"))}")
                }
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("CategorySelectorButton")
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.testTag("CategoryDropdown")
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category.name
                                    showCategoryDropdown = false
                                },
                                modifier = Modifier.testTag("CategoryItem_${category.name}")
                            )
                        }
                        if (categories.none { it.name == "Outros" }) {
                            DropdownMenuItem(
                                text = { Text("Outros") },
                                onClick = {
                                    selectedCategory = "Outros"
                                    showCategoryDropdown = false
                                },
                                modifier = Modifier.testTag("CategoryItem_Outros")
                            )
                        }
                    }
                }
                if (initialTransaction != null && onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth().testTag("DeleteTransactionButton")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = FinanceColors.expense)
                        Spacer(Modifier.width(8.dp))
                        Text("Excluir transação", color = FinanceColors.expense)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = amountText.replace(' ', '.').replace(',', '.').toDoubleOrNull() ?: 0.0
                    if (titleText.isNotBlank() && value != 0.0) {
                        onConfirm(titleText, value, selectedCategory, selectedDate)
                    }
                },
                modifier = Modifier.testTag("SaveTransactionButton")
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("CancelTransactionButton")) { Text("Cancelar") }
        }
    )
}

@Composable
private fun MonthYearPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var year by remember { mutableStateOf(initialYear) }
    var month by remember { mutableStateOf(initialMonth) }
    val locale = Locale.forLanguageTag("pt-BR")
    val monthNames = (1..12).map {
        java.time.Month.of(it).getDisplayName(java.time.format.TextStyle.SHORT, locale)
            .replaceFirstChar { c -> c.uppercase() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar mês e ano") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { year-- }) { Icon(Icons.Default.Remove, contentDescription = "Ano anterior") }
                    Text(year.toString(), style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = { year++ }) { Icon(Icons.Default.Add, contentDescription = "Próximo ano") }
                }
                monthNames.chunked(3).forEachIndexed { rowIdx, rowMonths ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowMonths.forEachIndexed { colIdx, name ->
                            val m = rowIdx * 3 + colIdx + 1
                            FilterChip(
                                selected = month == m,
                                onClick = { month = m },
                                label = { Text(name) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(year, month) },
                modifier = Modifier.testTag("DatePickerConfirm")
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("DatePickerCancel")
            ) { Text("Cancelar") }
        }
    )
}
