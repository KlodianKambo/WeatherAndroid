package klodian.kambo.weather

import androidx.lifecycle.*
import klodian.kambo.domain.Weather
import klodian.kambo.domain.WeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo
) : ViewModel() {

    private val weatherLiveData: MutableLiveData<List<Weather>> = MutableLiveData()

    fun getWeather(pattern: String): LiveData<List<Weather>> {
        viewModelScope.launch(Dispatchers.IO) {
            weatherLiveData.postValue(weatherRepo.getWeather(pattern))
        }
        return weatherLiveData
    }
}