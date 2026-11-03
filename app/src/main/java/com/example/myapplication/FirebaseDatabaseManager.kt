package com.example.myapplication

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseDatabaseManager {
    private const val TAG = "FirebaseDBManager"

    // Reference to Realtime Database
    private val realtimeDb by lazy { FirebaseDatabase.getInstance() }
    
    // Reference to Cloud Firestore
    private val firestoreDb by lazy { FirebaseFirestore.getInstance() }

    /**
     * Saves user registration or authentication logs to both Realtime Database and Firestore
     * to fulfill database logging requirements visible in console.
     */
    fun saveUserLoginLog(email: String, passwordHash: String) {
        val safeEmailKey = email.replace(".", ",").replace("@", "-")
        val timestamp = System.currentTimeMillis()
        
        val userData = mapOf(
            "email" to email,
            "passwordHash" to passwordHash,
            "lastLogin" to timestamp
        )

        // 1. Save to Realtime Database under 'users'
        realtimeDb.getReference("users").child(safeEmailKey).setValue(userData)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully synced user to Realtime Database.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to sync to Realtime Database", e)
            }

        // 2. Save to Firestore under 'users' collection
        firestoreDb.collection("users").document(email).set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully synced user to Cloud Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to sync to Cloud Firestore", e)
            }
    }

    /**
     * Pushes a local craft product addition to the database
     */
    fun uploadCraftProduct(title: String, price: String, seller: String, area: String = "") {
        val productData = mapOf(
            "title" to title,
            "price" to price,
            "sellerName" to seller,
            "sellerArea" to area,
            "timestamp" to System.currentTimeMillis()
        )

        // Firestore is the single marketplace source; Android caches pending writes.
        // Sync product to Firestore
        firestoreDb.collection("products").add(productData)
    }
}
