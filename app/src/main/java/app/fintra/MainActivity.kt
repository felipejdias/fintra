package app.fintra

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.fintra.ui.components.CategoryFilter
import app.fintra.ui.components.MonthFilter
import app.fintra.ui.components.SearchBar
import app.fintra.ui.components.ThemePickerDialog
import app.fintra.ui.components.TransactionDialog
import app.fintra.ui.components.YearFilter
import app.fintra.ui.screens.BreakdownScreen
import app.fintra.ui.screens.CategoriesScreen
import app.fintra.ui.screens.SummaryScreen
import app.fintra.ui.screens.TransactionsListScreen
import app.fintra.ui.theme.AppTheme
import app.fintra.ui.theme.PersonalFinanceTheme
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

enum class FinanceScreen {
    Transactions, Summary, Breakdown, Categories
}

val ptBrLocale = Locale("pt", "BR")

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(ptBrLocale)
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return formatter.format(amount)
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("fintra_prefs", Context.MODE_PRIVATE) }
            var appTheme by remember { mutableStateOf(AppTheme.fromKey(prefs.getString("theme", null))) }
            var showThemeDialog by remember { mutableStateOf(false) }

            PersonalFinanceTheme(appTheme = appTheme) {
                val viewModel: FinanceViewModel = viewModel()
                val allTransactions by viewModel.allTransactions.collectAsState(initial = emptyList())
                val allCategories by viewModel.allCategories.collectAsState(initial = emptyList())

                var searchQuery by remember { mutableStateOf("") }
                var selectedYear by remember { mutableStateOf<Int?>(null) }
                var selectedMonth by remember { mutableStateOf<Int?>(null) }
                var selectedCategoryName by remember { mutableStateOf<String?>(null) }
                var showDeleteDialog by remember { mutableStateOf(false) }
                var showExportDialog by remember { mutableStateOf(false) }
                var showAddTransactionDialog by remember { mutableStateOf(false) }
                var currentScreen by remember { mutableStateOf(FinanceScreen.Transactions) }

                val filePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let { viewModel.importCsv(context, it) }
                }

                val exportLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument("text/csv")
                ) { uri: Uri? ->
                    uri?.let {
                        viewModel.exportCsv(context, it, allTransactions)
                    }
                }

                val transactionsForList = allTransactions.filter {
                    val matchesSearch = it.title.contains(searchQuery, ignoreCase = true)
                    val matchesYear = selectedYear == null || it.date.year == selectedYear
                    val matchesMonth = selectedMonth == null || it.date.monthValue == selectedMonth
                    val matchesCategory = selectedCategoryName == null || it.categoryName == selectedCategoryName
                    matchesSearch && matchesYear && matchesMonth && matchesCategory
                }

                val transactionsForAnalytics = allTransactions.filter {
                    val matchesYear = selectedYear == null || it.date.year == selectedYear
                    val matchesMonth = selectedMonth == null || it.date.monthValue == selectedMonth
                    matchesYear && matchesMonth
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Limpar Dados") },
                        text = { Text("Tem certeza que deseja apagar todas as transações?") },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.clearAll()
                                showDeleteDialog = false
                            }, modifier = Modifier.testTag("ConfirmDeleteButton")) { Text("Confirmar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                        }
                    )
                }

                if (showExportDialog) {
                    var fileName by remember { mutableStateOf("backup_finance_${LocalDate.now()}.csv") }
                    AlertDialog(
                        onDismissRequest = { showExportDialog = false },
                        title = { Text("Exportar Backup") },
                        text = {
                            Column {
                                Text("Escolha o nome do arquivo:")
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = fileName,
                                    onValueChange = { fileName = it },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("ExportFileNameField")
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                exportLauncher.launch(fileName)
                                showExportDialog = false
                            }, modifier = Modifier.testTag("ExportConfirmButton")) { Text("Escolher Local") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showExportDialog = false }) { Text("Cancelar") }
                        }
                    )
                }

                if (showThemeDialog) {
                    ThemePickerDialog(
                        currentTheme = appTheme,
                        onThemeSelected = { theme ->
                            appTheme = theme
                            prefs.edit().putString("theme", theme.name).apply()
                        },
                        onDismiss = { showThemeDialog = false }
                    )
                }

                if (showAddTransactionDialog) {
                    TransactionDialog(
                        title = "Nova Transação",
                        categories = allCategories,
                        onDismiss = { showAddTransactionDialog = false },
                        onConfirm = { t, a, c, d ->
                            viewModel.addManualTransaction(t, a, c, d)
                            showAddTransactionDialog = false
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Column {
                            CenterAlignedTopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Fintra", fontWeight = FontWeight.Bold)
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { showThemeDialog = true }, modifier = Modifier.testTag("ThemePickerButton")) {
                                        Icon(Icons.Default.Palette, contentDescription = "Escolher tema")
                                    }
                                    IconButton(onClick = { showExportDialog = true }, modifier = Modifier.testTag("ExportBackupButton")) {
                                        Icon(Icons.Default.SaveAlt, contentDescription = "Exportar Backup")
                                    }
                                    IconButton(onClick = { filePickerLauncher.launch("text/*") }, modifier = Modifier.testTag("ImportCsvButton")) {
                                        Icon(Icons.Default.FileUpload, contentDescription = "Importar CSV")
                                    }
                                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.testTag("DeleteAllButton")) {
                                        Icon(Icons.Default.Delete, contentDescription = "Limpar Tudo")
                                    }
                                }
                            )

                            if (currentScreen != FinanceScreen.Categories) {
                                YearFilter(
                                    selectedYear = selectedYear,
                                    onYearSelected = {
                                        selectedYear = it
                                        selectedMonth = null
                                    },
                                    transactions = allTransactions
                                )
                                MonthFilter(
                                    selectedMonth = selectedMonth,
                                    selectedYear = selectedYear,
                                    onMonthSelected = { selectedMonth = it },
                                    transactions = allTransactions
                                )
                            }

                            if (currentScreen == FinanceScreen.Transactions) {
                                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
                                CategoryFilter(
                                    selectedCategory = selectedCategoryName,
                                    onCategorySelected = { selectedCategoryName = it },
                                    categories = allCategories
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (currentScreen == FinanceScreen.Transactions) {
                            ExtendedFloatingActionButton(
                                onClick = { showAddTransactionDialog = true },
                                icon = { Icon(Icons.Default.Add, contentDescription = "Adicionar Transação") },
                                text = { Text("Nova") },
                                modifier = Modifier.testTag("AddTransactionFAB")
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                                label = { Text("Transações") },
                                selected = currentScreen == FinanceScreen.Transactions,
                                onClick = { currentScreen = FinanceScreen.Transactions },
                                modifier = Modifier.testTag("NavTransactions")
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                                label = { Text("Resumo") },
                                selected = currentScreen == FinanceScreen.Summary,
                                onClick = { currentScreen = FinanceScreen.Summary },
                                modifier = Modifier.testTag("NavSummary")
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.PieChart, contentDescription = null) },
                                label = { Text("Gastos") },
                                selected = currentScreen == FinanceScreen.Breakdown,
                                onClick = { currentScreen = FinanceScreen.Breakdown },
                                modifier = Modifier.testTag("NavBreakdown")
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Category, contentDescription = null) },
                                label = { Text("Categorias") },
                                selected = currentScreen == FinanceScreen.Categories,
                                onClick = { currentScreen = FinanceScreen.Categories },
                                modifier = Modifier.testTag("NavCategories")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        when (currentScreen) {
                            FinanceScreen.Transactions -> TransactionsListScreen(transactionsForList, allCategories, viewModel)
                            FinanceScreen.Summary -> SummaryScreen(transactionsForAnalytics)
                            FinanceScreen.Breakdown -> BreakdownScreen(transactionsForAnalytics)
                            FinanceScreen.Categories -> CategoriesScreen(allCategories, viewModel)
                        }
                    }
                }
            }
        }
    }
}
