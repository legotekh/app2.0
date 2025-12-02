package com.example.myapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "places",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val categoryId: Int
)


data class CategoryModel(val id: Int, val name: String)
data class PlaceModel(val id: Int, val title: String, val description: String, val categoryName: String)

sealed class ListItem {
    data class HeaderItem(val title: String) : ListItem()
    data class HorizontalCarouselItem(val categories: List<CategoryModel>) : ListItem()
    data class PlaceItem(val place: PlaceModel) : ListItem()
}