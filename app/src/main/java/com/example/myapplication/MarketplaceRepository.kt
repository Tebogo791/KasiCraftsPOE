package com.example.myapplication

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges

object MarketplaceRepository {
    fun observeProducts(
        onProducts: (List<Product>, Boolean) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = FirebaseFirestore.getInstance()
        .collection("products")
        .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            val products = snapshot.documents.mapIndexedNotNull { index, doc ->
                val title = doc.getString("title") ?: return@mapIndexedNotNull null
                val rawPrice = doc.getString("price") ?: doc.get("price")?.toString() ?: "0"
                Product(
                    id = index + 1,
                    title = title,
                    body = doc.getString("description") ?: "",
                    price = if (rawPrice.startsWith("R")) rawPrice else "R $rawPrice",
                    sellerName = doc.getString("sellerName") ?: "Local Artisan",
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            }
            onProducts(products, snapshot.metadata.isFromCache)
        }
}
