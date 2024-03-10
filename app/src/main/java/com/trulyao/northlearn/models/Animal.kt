package com.trulyao.northlearn.models

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

const val API_URL = "https://animapi.fly.dev"

// Base retrofit instance which is then reused by the service (i.e implements the service)
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(API_URL)
    .build()

data class Animal(val name: String, val slugifiedName: String, val filename: String)

interface AnimalAPIService {
    @GET("animals.json")
    suspend fun getAnimals(): String
}

// Create retrofit singleton - this will be reused across all API calls
object AnimalsAPI {
    val retrofitService: AnimalAPIService by lazy {
        retrofit.create(AnimalAPIService::class.java)
    }
}