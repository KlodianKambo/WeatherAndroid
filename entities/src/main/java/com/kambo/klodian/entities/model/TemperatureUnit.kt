package com.kambo.klodian.entities.model

sealed class TemperatureUnit(val symbol: String) {
    object Fahrenheit : TemperatureUnit("F")
    object Celsius : TemperatureUnit("C")
}
