package klodian.kambo.data.model

internal data class WeatherDto(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)