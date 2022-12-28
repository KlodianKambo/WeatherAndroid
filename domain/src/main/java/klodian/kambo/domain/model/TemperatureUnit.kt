package klodian.kambo.domain.model

sealed class TemperatureUnit(val symbol: String) {
    object Fahrenheit : TemperatureUnit("F")
    object Celsius : TemperatureUnit("C")
}
