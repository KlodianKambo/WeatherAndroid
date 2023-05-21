package com.kambo.klodian.entities.model

data class Weather(
    val id: Long,
    val weather: String,
    val description: String,
    val iconName: String
)