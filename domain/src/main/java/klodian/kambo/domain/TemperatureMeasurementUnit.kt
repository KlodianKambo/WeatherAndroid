package klodian.kambo.domain

sealed class TemperatureMeasurementUnit(val symbol: String) {
    object Fahrenheit : TemperatureMeasurementUnit("F")
    object Celsius : TemperatureMeasurementUnit("C")
}