package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartManagerTest {

    @Before
    fun setUp() {
        CartManager.clearCart()
    }

    @Test
    fun testAddItem_IncreasesItemCount() {
        val product = Product(id = 1, title = "Test Basket", price = "R 100.00")
        CartManager.addItem(product)
        assertEquals(1, CartManager.getItemCount())
    }

    @Test
    fun testCalculateSubtotal_CorrectSum() {
        val p1 = Product(id = 1, title = "Basket", price = "R 150.00")
        val p2 = Product(id = 2, title = "Mug", price = "R 50.50")
        CartManager.addItem(p1)
        CartManager.addItem(p2)
        assertEquals(200.50, CartManager.calculateSubtotal(), 0.01)
    }

    @Test
    fun testRemoveItem_DecreasesCount() {
        val p1 = Product(id = 1, title = "Basket", price = "R 100.00")
        CartManager.addItem(p1)
        CartManager.removeItemAt(0)
        assertEquals(0, CartManager.getItemCount())
    }
}