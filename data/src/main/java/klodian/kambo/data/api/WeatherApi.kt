package klodian.kambo.data.api

import klodian.kambo.data.model.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") cityNamePattern: String,
        @Query("lang") language: String,
        @Query("cnt") limit: Int = 5,
        @Query("units") units: String
    ): WeatherResponseDto
}