package klodian.kambo.data.model

internal data class ForecastResponseDto(
    val list: List<WeatherResponseDto>,
    val city: CityDto
)