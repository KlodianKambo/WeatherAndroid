package klodian.kambo.domain

data class Weather(
    val id: Long,
    val weather: String,
    val description: String,
    val iconName: String
)