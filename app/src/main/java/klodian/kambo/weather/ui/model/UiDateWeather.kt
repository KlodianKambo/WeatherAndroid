package klodian.kambo.weather.ui.model

data class UiDateWeather(
    val displayableDay: String,
    val uiWeatherTemperatureList: List<UiWeatherTemperature>
)