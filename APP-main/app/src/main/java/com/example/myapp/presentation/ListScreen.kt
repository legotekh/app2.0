package com.example.myapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapp.data.CategoryModel
import com.example.myapp.data.ListItem
import com.example.myapp.data.PlaceModel
import com.example.myapp.data.CategoryEntity

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel
) {
    val items by viewModel.screenItems.collectAsState()
    val categories by viewModel.categoriesForSelection.collectAsState(initial = emptyList())

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddPlaceDialog by remember { mutableStateOf(false) }

    var categoryToEdit by remember { mutableStateOf<CategoryModel?>(null) }
    var placeToEdit by remember { mutableStateOf<PlaceModel?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp, start = 16.dp, end = 16.dp), // місце для FAB
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                when (item) {
                    is ListItem.HeaderItem -> HeaderView(item.title)
                    is ListItem.HorizontalCarouselItem -> CarouselView(
                        categories = item.categories,
                        onDelete = { viewModel.deleteCategory(it) },
                        onEdit = { categoryToEdit = it }
                    )
                    is ListItem.PlaceItem -> PlaceCardView(
                        place = item.place,
                        onDelete = {
                            val catId = categories.find { it.name == item.place.categoryName }?.id ?: 0
                            viewModel.deletePlace(item.place, catId)
                        },
                        onEdit = { placeToEdit = it }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                icon = { Icon(Icons.Default.Add, "Add Cat") },
                text = { Text("Категорія") }
            )
            ExtendedFloatingActionButton(
                onClick = { showAddPlaceDialog = true },
                icon = { Icon(Icons.Default.Add, "Add Place") },
                text = { Text("Місце") },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }


    if (showAddCategoryDialog) {
        InputNameDialog(
            title = "Нова категорія",
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name -> viewModel.addCategory(name); showAddCategoryDialog = false }
        )
    }

    if (categoryToEdit != null) {
        InputNameDialog(
            title = "Редагувати категорію",
            initialName = categoryToEdit!!.name,
            onDismiss = { categoryToEdit = null },
            onConfirm = { name ->
                viewModel.editCategory(categoryToEdit!!, name)
                categoryToEdit = null
            }
        )
    }

    if (showAddPlaceDialog) {
        if (categories.isEmpty()) {
            AlertDialog(
                onDismissRequest = { showAddPlaceDialog = false },
                confirmButton = { TextButton(onClick = { showAddPlaceDialog = false }) { Text("OK") } },
                title = { Text("Увага") },
                text = { Text("Спочатку створіть хоча б одну категорію!") }
            )
        } else {
            AddPlaceDialog(
                categories = categories,
                onDismiss = { showAddPlaceDialog = false },
                onConfirm = { title, desc, catId ->
                    viewModel.addPlace(title, desc, catId)
                    showAddPlaceDialog = false
                }
            )
        }
    }

    if (placeToEdit != null) {
        val currentCatId = categories.find { it.name == placeToEdit!!.categoryName }?.id ?: categories.firstOrNull()?.id ?: 0
        AddPlaceDialog(
            categories = categories,
            initialTitle = placeToEdit!!.title,
            initialDesc = placeToEdit!!.description,
            initialCatId = currentCatId,
            isEdit = true,
            onDismiss = { placeToEdit = null },
            onConfirm = { title, desc, catId ->
                viewModel.editPlace(placeToEdit!!, title, desc, catId)
                placeToEdit = null
            }
        )
    }
}


@Composable
fun HeaderView(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun CarouselView(
    categories: List<CategoryModel>,
    onDelete: (CategoryModel) -> Unit,
    onEdit: (CategoryModel) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape,
                modifier = Modifier.height(40.dp).clickable { onEdit(category) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    IconButton(onClick = { onDelete(category) }) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceCardView(
    place: PlaceModel,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = place.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = place.categoryName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(text = place.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, maxLines = 2)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = Color.Gray)
            }
        }
    }
}


@Composable
fun InputNameDialog(title: String, initialName: String = "", onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { if(text.isNotBlank()) onConfirm(text) }) { Text("Зберегти") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Скасувати") } },
        title = { Text(title) },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Назва") }) }
    )
}

@Composable
fun AddPlaceDialog(
    categories: List<CategoryEntity>,
    initialTitle: String = "",
    initialDesc: String = "",
    initialCatId: Int = categories.firstOrNull()?.id ?: 0,
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var desc by remember { mutableStateOf(initialDesc) }
    var selectedCatId by remember { mutableIntStateOf(initialCatId) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { if(title.isNotBlank()) onConfirm(title, desc, selectedCatId) }) { Text("Зберегти") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Скасувати") } },
        title = { Text(if(isEdit) "Редагувати місце" else "Додати місце") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Назва") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Опис") })

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(text = categories.find { it.id == selectedCatId }?.name ?: "Оберіть категорію")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = { selectedCatId = cat.id; expanded = false }
                            )
                        }
                    }
                }
            }
        }
    )
}