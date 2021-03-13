package klodian.kambo.data

data class WeatherDto(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)