package com.example.myapplication

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges

object MarketplaceRepository {

    val sampleProducts = listOf(
        Product(
            id = 1001,
            title = "Zulu Woven Basket",
            body = "Handcrafted traditional Zulu basket woven from natural palm leaves.",
            price = "R 250.00",
            sellerName = "Nomsa Dlamini",
            sellerArea = "Durban",
            imageResId = R.drawable.zulu_basket,
            category = "Baskets",
            isSample = true
        ),
        Product(
            id = 1002,
            title = "Handcrafted Wooden Mug",
            body = "Carved hardwood mug, polished with organic beeswax.",
            price = "R 180.00",
            sellerName = "Sipho Mabena",
            sellerArea = "Soweto",
            imageResId = R.drawable.wooden_mug,
            category = "Pottery",
            isSample = true
        ),
        Product(
            id = 1003,
            title = "Beaded Zulu Necklace",
            body = "Vibrant traditional beaded jewelry handcrafted with precision.",
            price = "R 120.00",
            sellerName = "Thandi Khumalo",
            sellerArea = "Johannesburg",
            imageResId = R.drawable.beaded_necklace,
            category = "Jewelry",
            isSample = true
        ),
        Product(
            id = 1004,
            title = "Hand-carved Wooden Elephant",
            body = "Detailed African elephant sculpture carved from indigenous wood.",
            price = "R 350.00",
            sellerName = "Khaboro Tau",
            sellerArea = "Cape Town",
            imageResId = R.drawable.wooden_elephant,
            category = "Textiles",
            isSample = true
        )
    )

    fun observeProducts(
        onProducts: (List<Product>, Boolean) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = FirebaseFirestore.getInstance()
        .collection("products")
        .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
            if (error != null) {
                // If Firestore error occurs, return sample products as fallback
                onProducts(sampleProducts, false)
                onError(error)
                return@addSnapshotListener
            }
            if (snapshot == null) {
                onProducts(sampleProducts, false)
                return@addSnapshotListener
            }
            val realProducts = snapshot.documents.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                val rawPrice = doc.getString("price") ?: doc.get("price")?.toString() ?: "0"
                Product(
                    id = doc.id.hashCode(),
                    title = title,
                    body = doc.getString("description") ?: "",
                    price = if (rawPrice.startsWith("R")) rawPrice else "R $rawPrice",
                    sellerName = doc.getString("sellerName") ?: "Local Artisan",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    sellerArea = doc.getString("sellerArea") ?: "",
                    sellerUid = doc.getString("sellerUid") ?: "",
                    category = doc.getString("category") ?: "Baskets",
                    isSample = false
                )
            }
            // Combine real uploaded products with sample products so marketplace always has content
            val allProducts = realProducts + sampleProducts
            onProducts(allProducts, snapshot.metadata.isFromCache)
        }
}
