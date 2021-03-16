package klodian.kambo.weather.model

import androidx.annotation.DrawableRes
import klodian.kambo.weather.R

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