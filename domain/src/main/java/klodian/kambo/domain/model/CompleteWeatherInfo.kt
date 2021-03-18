package klodian.kambo.domain.model

import java.time.LocalDateTime

data class CompleteWeatherInfo(
    val date: LocalDateTime,
    val weather: List<Weather>,
    val temperature: Temperature
)