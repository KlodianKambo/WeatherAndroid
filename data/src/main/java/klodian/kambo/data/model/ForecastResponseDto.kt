package klodian.kambo.data.model

data class ForecastResponseDto(
    val list: List<WeatherResponseDto>,
    val city: CityDto
)