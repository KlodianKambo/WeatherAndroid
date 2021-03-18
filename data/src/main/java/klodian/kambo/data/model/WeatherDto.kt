package klodian.kambo.data.model

data class WeatherDto(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)