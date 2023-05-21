package klodian.kambo.data.api

import klodian.kambo.data.model.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/forecast")
    suspend fun getWeather(
        @Query("q") cityNamePattern: String,
        @Query("lang") language: String,
        @Query("units") units: String
    ): ForecastResponseDto

    @GET("data/2.5/forecast")
    suspend fun getWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String
    ): ForecastResponseDto
}