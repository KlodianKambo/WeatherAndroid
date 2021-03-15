package klodian.kambo.weather.model

data class UiTemperature(
    val temperature: String,
    val feelsLike: String,
    val minTemperature: String,
    val maxTemperature: String,
    val pressure: String,
    val humidity: String
)

sealed class TemperatureMeasurementUnit(val symbol: String) {
    object Fahrenheit : TemperatureMeasurementUnit("F")
    object Celsius : TemperatureMeasurementUnit("C")
}