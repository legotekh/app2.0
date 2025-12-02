package com.example.myapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    val screenItems: StateFlow<List<ListItem>> = combine(
        repository.allCategories,
        repository.allPlaces
    ) { categories, places ->
        val items = mutableListOf<ListItem>()

        items.add(ListItem.HeaderItem("Категорії"))
        if (categories.isNotEmpty()) {
            val catModels = categories.map { CategoryModel(it.id, it.name) }
            items.add(ListItem.HorizontalCarouselItem(catModels))
        } else {
            items.add(ListItem.HeaderItem("(Немає категорій)"))
        }

        items.add(ListItem.HeaderItem("Місця"))
        if (places.isNotEmpty()) {
            items.addAll(places.map { ListItem.PlaceItem(it) })
        } else {
            items.add(ListItem.HeaderItem("(Немає місць)"))
        }

        items
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addCategory(name: String) = viewModelScope.launch {
        repository.insertCategory(name)
    }

    fun deleteCategory(category: CategoryModel) = viewModelScope.launch {
        repository.deleteCategory(category.id, category.name)
    }

    fun editCategory(category: CategoryModel, newName: String) = viewModelScope.launch {
        repository.updateCategory(category.id, newName)
    }

    fun addPlace(title: String, description: String, categoryId: Int) = viewModelScope.launch {
        repository.insertPlace(title, description, categoryId)
    }

    fun deletePlace(place: PlaceModel, categoryId: Int) = viewModelScope.launch {
        val entity = PlaceEntity(place.id, place.title, place.description, categoryId)
        repository.deletePlace(entity)
    }

    fun editPlace(place: PlaceModel, newTitle: String, newDesc: String, categoryId: Int) = viewModelScope.launch {
        val entity = PlaceEntity(place.id, newTitle, newDesc, categoryId)
        repository.updatePlace(entity)
    }

    val categoriesForSelection: Flow<List<CategoryEntity>> = repository.allCategories
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}