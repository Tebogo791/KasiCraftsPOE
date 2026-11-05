package com.example.myapplication

object CartManager {
    private val cartItems = mutableListOf<Product>()

    fun addItem(product: Product) {
        cartItems.add(product)
    }

    fun removeItemAt(position: Int) {
        if (position in cartItems.indices) {
            cartItems.removeAt(position)
        }
    }

    fun getItems(): List<Product> {
        return cartItems
    }

    fun getItemCount(): Int = cartItems.size

    fun clearCart() {
        cartItems.clear()
    }

    fun calculateSubtotal(): Double {
        var subtotal = 0.0
        for (item in cartItems) {
            // Extracts numerical digits from currency string (e.g. "R 250.00" -> 250.0)
            val numericString = item.price.replace(Regex("[^0-9.]"), "")
            val parsedPrice = numericString.toDoubleOrNull() ?: 0.0
            subtotal += parsedPrice
        }
        return subtotal
    }
}