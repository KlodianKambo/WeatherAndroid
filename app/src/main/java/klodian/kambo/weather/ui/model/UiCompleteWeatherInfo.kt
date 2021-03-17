package klodian.kambo.weather.ui.model

data class UiCompleteWeatherInfo(
    val uiWeatherTemperatureList: List<UiWeatherTemperature>,
    val cityNameResult: String,
    val displayableTimeStamp: String
)