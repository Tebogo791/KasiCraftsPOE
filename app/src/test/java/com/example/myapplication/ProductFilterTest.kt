package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFilterTest {

    private val products = listOf(
        Product(id = 1, title = "Zulu Woven Basket", category = "Baskets", sellerArea = "Durban"),
        Product(id = 2, title = "Wooden Mug", category = "Pottery", sellerArea = "Soweto"),
        Product(id = 3, title = "Beaded Zulu Necklace", category = "Jewelry", sellerArea = "Johannesburg")
    )

    @Test
    fun testCategoryFilter_MatchesCategory() {
        val filtered = products.filter { it.category == "Baskets" }
        assertEquals(1, filtered.size)
        assertEquals("Zulu Woven Basket", filtered[0].title)
    }

    @Test
    fun testSearchQuery_MatchesTitleCaseInsensitive() {
        val query = "zulu"
        val filtered = products.filter { it.title.contains(query, ignoreCase = true) }
        assertEquals(2, filtered.size)
    }

    @Test
    fun testAreaFilter_MatchesSellerArea() {
        val area = "Soweto"
        val filtered = products.filter { it.sellerArea.equals(area, ignoreCase = true) }
        assertEquals(1, filtered.size)
        assertEquals("Wooden Mug", filtered[0].title)
    }
}