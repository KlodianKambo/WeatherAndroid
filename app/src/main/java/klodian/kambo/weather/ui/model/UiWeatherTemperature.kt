package klodian.kambo.weather.ui.model


data class UiWeatherTemperature(
    val id: String,
    val displayableHour: String,
    val weather: UiWeather,
    val temperature: UiTemperature
)