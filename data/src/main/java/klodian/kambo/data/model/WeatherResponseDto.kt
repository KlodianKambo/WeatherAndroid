package klodian.kambo.data.model

import com.google.gson.annotations.SerializedName

internal data class WeatherResponseDto(
    @SerializedName("dt")
    val dateLongMillis: Long,
    val weather: List<WeatherDto>,
    @SerializedName("main")
    val main: TemperatureDto
)