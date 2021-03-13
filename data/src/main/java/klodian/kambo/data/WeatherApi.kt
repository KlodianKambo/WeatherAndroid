package klodian.kambo.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getSongs(
        @Query("q") cityNamePattern: String,
        @Query("cnt") limit: Int = 5
    ): WeatherResponseDto
}