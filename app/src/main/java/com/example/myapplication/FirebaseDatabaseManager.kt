package com.example.myapplication

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

object FirebaseDatabaseManager {
    private const val TAG = "FirebaseDBManager"

    // Reference to Realtime Database
    private val realtimeDb by lazy { FirebaseDatabase.getInstance() }
    
    // Reference to Cloud Firestore
    private val firestoreDb by lazy { FirebaseFirestore.getInstance() }

    /**
     * Legacy login logging deliberately disabled.
     * Passwords and password hashes must never be written to client-accessible databases.
     * Firebase Authentication handles credentials.
     */
    @Deprecated("Do not store credentials or password hashes")
    fun saveUserLoginLog(email: String, passwordHash: String) {
        Log.w(TAG, "Legacy login logging disabled")
    }

    /**
     * Pushes a local craft product addition to the database
     */
    fun uploadCraftProduct(
        title: String,
        price: String,
        seller: String,
        area: String = ""
    ): Task<DocumentReference> {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw IllegalStateException("Sign in to list a craft")
        val productData = mapOf(
            "title" to title,
            "price" to price,
            "sellerName" to seller,
            "sellerArea" to area,
            "sellerUid" to user.uid,
            "sellerEmailVerified" to user.isEmailVerified,
            "timestamp" to System.currentTimeMillis()
        )
        return firestoreDb.collection("products").add(productData)
    }
}
