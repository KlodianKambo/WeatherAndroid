package klodian.kambo.data

import klodian.kambo.domain.WeatherRepo
import javax.inject.Inject



class WeatherRepoImpl @Inject constructor(private val weatherApi: WeatherApi): WeatherRepo {
}