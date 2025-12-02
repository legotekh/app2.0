package com.example.myapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("""
        SELECT places.id, places.title, places.description, categories.name as categoryName 
        FROM places 
        INNER JOIN categories ON places.categoryId = categories.id
    """)
    fun getAllPlacesWithCategories(): Flow<List<PlaceModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Update
    suspend fun updatePlace(place: PlaceEntity)
}