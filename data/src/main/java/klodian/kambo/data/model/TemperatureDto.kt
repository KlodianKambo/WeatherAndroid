package klodian.kambo.data.model

import com.google.gson.annotations.SerializedName

internal data class TemperatureDto(
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