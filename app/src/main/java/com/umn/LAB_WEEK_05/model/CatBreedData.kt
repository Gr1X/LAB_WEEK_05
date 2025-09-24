package com.example.lab_week_05.model

import com.squareup.moshi.Json

data class CatBreedData(
    val id: String,
    val name: String,
    val temperament: String,
    val origin: String,
    @field:Json(name = "life_span") val lifeSpan: String
)