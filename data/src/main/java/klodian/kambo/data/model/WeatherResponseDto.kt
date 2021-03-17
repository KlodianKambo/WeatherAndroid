package klodian.kambo.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    val weather: List<WeatherDto>,
    @SerializedName("main")
    val main: TemperatureDto
)

data class TemperatureDto(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val minTemp: Double,
    @SerializedName("temp_max")
    val maxTemp: Double,
    val pressure: Double,
    val humidity: Double
)