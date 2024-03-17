package com.trulyao.northlearn.models

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

public const val API_URL = "https://animapi.fly.dev"
private val json = Json { ignoreUnknownKeys = true }

// Base retrofit instance which is then reused by the service (i.e implements the service)
private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(API_URL)
    .build()

@Serializable
data class Animal(
    val name: String,

    @SerialName("slugified_name")
    val slugifiedName: String,

    val filename: String,
)

interface AnimalAPIService {
    @GET("animals.json")
    suspend fun getAnimals(): List<Animal>
}

// Create retrofit singleton - this will be reused across all API calls
object AnimalsAPI {
    val retrofitService: AnimalAPIService by lazy {
        retrofit.create(AnimalAPIService::class.java)
    }
}