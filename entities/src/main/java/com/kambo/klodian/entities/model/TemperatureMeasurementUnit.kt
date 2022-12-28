package com.kambo.klodian.entities.model

sealed class TemperatureMeasurementUnit(val symbol: String) {
    object Fahrenheit : TemperatureMeasurementUnit("F")
    object Celsius : TemperatureMeasurementUnit("C")
}