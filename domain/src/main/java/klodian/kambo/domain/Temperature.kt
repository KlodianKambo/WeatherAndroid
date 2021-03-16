package klodian.kambo.domain

data class Temperature(
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Double,
    val humidity: Double,
    val temperatureMeasurementUnit: TemperatureMeasurementUnit
)