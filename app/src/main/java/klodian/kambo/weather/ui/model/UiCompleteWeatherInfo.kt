package klodian.kambo.weather.ui.model

data class UiCompleteWeatherInfo(
    val weather: List<UiWeather>,
    val temperature: UiTemperature,
    val cityNameResult: String,
    val displayableTimeStamp: String
)