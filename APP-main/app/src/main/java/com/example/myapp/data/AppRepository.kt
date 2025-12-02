package com.example.myapp.data

class AppRepository {

    private fun getRawCategories(): List<CategoryModel> {
        return listOf(
            CategoryModel(1, "Всі"),
            CategoryModel(2, "Парки"),
            CategoryModel(3, "Кафе"),
            CategoryModel(4, "Музеї"),
            CategoryModel(5, "Готелі")
        )
    }

    private fun getRawPlaces(): List<PlaceModel> {
        return List(20) { index ->
            PlaceModel(
                id = index,
                title = "Локація №${index + 1}",
                description = "Опис чудової локації під номером ${index + 1}"
            )
        }
    }

    fun getScreenContent(): List<ListItem> = buildList {

        add(ListItem.HeaderItem("Категорії"))

        add(ListItem.HorizontalCarouselItem(getRawCategories()))

        add(ListItem.HeaderItem("Популярні місця"))

        val places = getRawPlaces()
        places.forEach { place ->
            add(ListItem.PlaceItem(place))
        }
    }
}