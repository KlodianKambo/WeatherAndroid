package klodian.kambo.weather.ui.model

data class UiCompleteWeatherInfo(
    val uiDateWeather: List<UiDateWeather>,
    val cityNameResult: String,
    val displayableTimeStamp: String
)

