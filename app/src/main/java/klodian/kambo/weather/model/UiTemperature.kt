package klodian.kambo.weather.model

data class UiTemperature(
    val temperature: String,
    val feelsLike: String,
    val minTemperature: String,
    val maxTemperature: String,
    val pressure: String,
    val humidity: String,
    val measurementUnit: TemperatureMeasurementUnit
)

sealed class TemperatureMeasurementUnit {
    object Fahrenheit : TemperatureMeasurementUnit()
    object Celsius : TemperatureMeasurementUnit()
}