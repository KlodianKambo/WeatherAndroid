package klodian.kambo.weather.model

data class UiTemperature(
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Double,
    val humidity: Double,
    val measurementUnit: TemperatureMeasurementUnit
)

sealed class TemperatureMeasurementUnit {
    object Fahrenheit : TemperatureMeasurementUnit()
    object Celsius : TemperatureMeasurementUnit()
}