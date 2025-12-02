package com.example.myapp.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val allCategories: Flow<List<CategoryEntity>> = appDao.getAllCategories()
    val allPlaces: Flow<List<PlaceModel>> = appDao.getAllPlacesWithCategories()

    suspend fun insertCategory(name: String) {
        appDao.insertCategory(CategoryEntity(name = name))
    }

    suspend fun updateCategory(id: Int, name: String) {
        appDao.updateCategory(CategoryEntity(id = id, name = name))
    }

    suspend fun deleteCategory(id: Int, name: String) {
        appDao.deleteCategory(CategoryEntity(id = id, name = name))
    }

    suspend fun insertPlace(title: String, description: String, categoryId: Int) {
        appDao.insertPlace(PlaceEntity(title = title, description = description, categoryId = categoryId))
    }

    suspend fun updatePlace(id: Int, title: String, description: String, categoryId: Int) {
        appDao.updatePlace(PlaceEntity(id = id, title = title, description = description, categoryId = categoryId))
    }

    suspend fun deletePlace(id: Int) {
    }

    suspend fun deletePlace(place: PlaceEntity) {
        appDao.deletePlace(place)
    }
}