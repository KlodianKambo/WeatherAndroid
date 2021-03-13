package klodian.kambo.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klodian.kambo.domain.Weather
import klodian.kambo.domain.WeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo
) : ViewModel() {

    private val weatherLiveData: MutableLiveData<List<UiWeather>> = MutableLiveData()

    fun getWeather(pattern: String): LiveData<List<UiWeather>> {
        viewModelScope.launch(Dispatchers.IO) {
            weatherLiveData.postValue(weatherRepo.getWeather(pattern).map { it.toUiWeather() })
        }
        return weatherLiveData
    }

    private fun Weather.toUiWeather(): UiWeather {
        return UiWeather(
            id = id,
            title = weather,
            description = description,
            iconPath = iconName
        )
    }
}