package com.kambo.klodian.ui.ui.model

import androidx.annotation.DrawableRes
import com.kambo.klodian.ui.R

data class UiTemperature(
    val displayableTemperature: String,
    val displayableFeelsLike: String,
    val displayableMinTemperature: String,
    val displayableMaxTemperature: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: String,
    val humidity: String
)

sealed class UiTemperatureMeasurementUnit(@DrawableRes val iconResId: Int) {
    object Fahrenheit : UiTemperatureMeasurementUnit(R.drawable.ic_temperature_fahrenheit)
    object Celsius : UiTemperatureMeasurementUnit(R.drawable.ic_temperature_celsius)
}