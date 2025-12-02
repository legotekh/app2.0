package com.example.myapp.data

data class CategoryModel(val id: Int, val name: String)
data class PlaceModel(val id: Int, val title: String, val description: String)

sealed class ListItem {
    data class HeaderItem(val title: String) : ListItem()

    data class HorizontalCarouselItem(val categories: List<CategoryModel>) : ListItem()

    data class PlaceItem(val place: PlaceModel) : ListItem()
}