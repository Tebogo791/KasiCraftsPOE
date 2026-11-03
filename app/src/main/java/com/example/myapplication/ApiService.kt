package com.example.myapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Data Class
data class Product(
    val id: Int,
    val title: String,
    val body: String,
    val price: String = "R 0.00",
    val sellerName: String = "Local Artisan",
    val imageUrl: String = "https://picsum.photos/200",
    val imageResId: Int? = null,
    val sellerArea: String = "",
    val sellerUid: String = ""
)

// API Interface
interface ApiService {
    @GET("posts")
    suspend fun getProducts(): List<Product>
}

// Retrofit Instance
object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}