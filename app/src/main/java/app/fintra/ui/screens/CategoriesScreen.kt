package app.fintra.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.fintra.Category
import app.fintra.FinanceViewModel
import app.fintra.ui.theme.FinanceColors
import app.fintra.ui.theme.categoryColor
import app.fintra.ui.theme.categoryIcon

@Composable
fun CategoriesScreen(categories: List<Category>, viewModel: FinanceViewModel) {
    var newCategoryName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Gerenciar Categorias",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                modifier = Modifier.weight(1f).testTag("NewCategoryField"),
                placeholder = { Text("Nova categoria") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                onClick = {
                    if (newCategoryName.isNotBlank()) {
                        viewModel.addCategory(newCategoryName)
                        newCategoryName = ""
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(52.dp).testTag("AddCategoryButton")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Categoria")
            }
        }
        LazyColumn(
            modifier = Modifier.testTag("CategoriesList"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryCard(category, viewModel)
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, viewModel: FinanceViewModel) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(category.name) }
    val accent = categoryColor(category.name)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FinanceColors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("CategoryItemRow_${category.name}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("EditCategoryField")
                )
                IconButton(
                    onClick = {
                        if (editedName.isNotBlank() && editedName != category.name) {
                            viewModel.updateCategory(category.name, category.copy(name = editedName))
                        }
                        isEditing = false
                    },
                    modifier = Modifier.testTag("SaveCategoryEditButton")
                ) { Icon(Icons.Default.Check, contentDescription = "Salvar", tint = FinanceColors.income) }
                IconButton(
                    onClick = { isEditing = false; editedName = category.name },
                    modifier = Modifier.testTag("CancelCategoryEditButton")
                ) { Icon(Icons.Default.Close, contentDescription = "Cancelar") }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accent.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon(category.name),
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    category.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).testTag("CategoryNameText_${category.name}")
                )
                if (category.name != "Renda") {
                    IconButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.testTag("EditCategoryButton_${category.name}")
                    ) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                    IconButton(
                        onClick = { viewModel.deleteCategory(category) },
                        modifier = Modifier.testTag("DeleteCategoryButton_${category.name}")
                    ) { Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = FinanceColors.expense) }
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Categoria Fixa",
                        modifier = Modifier.padding(12.dp).size(18.dp).testTag("LockIcon_Renda"),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
