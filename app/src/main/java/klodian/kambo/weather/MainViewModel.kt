package klodian.kambo.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import klodian.kambo.domain.*
import klodian.kambo.weather.model.TemperatureMeasurementUnit
import klodian.kambo.weather.model.UiCompleteWeatherInfo
import klodian.kambo.weather.model.UiTemperature
import klodian.kambo.weather.model.UiWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo,
    private val getValidSearchPatternUseCase: GetValidSearchPatternUseCase
) : ViewModel() {

    sealed class SearchError(
        @StringRes val errorMessageResId: Int,
        @DrawableRes val iconResId: Int? = null
    ) {
        object FieldCannotBeNull : SearchError(R.string.search_input_error_empty)
        object Only3ParamsAreAllowed : SearchError(R.string.search_input_error_too_many_params)
        object PleaseInsertTheCity : SearchError(R.string.search_input_error_no_param_found)

        object NoInternet :
            SearchError(R.string.search_error_no_internet, R.drawable.ic_baseline_cloud_off)

        data class WeatherNotFound(val searchValue: String) :
            SearchError(R.string.search_error_not_found, R.drawable.ic_baseline_live_help)

        object Generic :
            SearchError(R.string.search_error_generic, R.drawable.ic_baseline_error_outline)
    }

    private val weatherLiveData: MutableLiveData<Either<SearchError, UiCompleteWeatherInfo>> =
        MutableLiveData()

    private val loadingLiveData = MutableLiveData(false)

    fun getWeatherResult(): LiveData<Either<SearchError, UiCompleteWeatherInfo>> = weatherLiveData
    fun isLoading(): LiveData<Boolean> = loadingLiveData

    fun getWeather(pattern: String, locale: Locale) {
        getValidSearchPatternUseCase(pattern).fold(
            ifRight = { correctedPattern ->
                loadingLiveData.postValue(true)

                viewModelScope.launch(Dispatchers.IO) {
                    weatherRepo.getWeather(correctedPattern, locale).fold(
                        ifLeft = { safeRequestError ->
                            loadingLiveData.postValue(false)
                            weatherLiveData.postValue(
                                Either.left(
                                    safeRequestError.toSearchError(correctedPattern)
                                )
                            )
                        },
                        ifRight = { completeWeatherInfo ->
                            loadingLiveData.postValue(false)
                            weatherLiveData.postValue(
                                Either.right(
                                    UiCompleteWeatherInfo(
                                        weather = completeWeatherInfo.weather.map { weather -> weather.toUiWeather() },
                                        temperature = completeWeatherInfo.temperature.toUiTemperature()
                                    )
                                )
                            )
                        })
                }
            },
            ifLeft = { patternValidationError ->
                weatherLiveData.postValue(Either.left(patternValidationError.toSearchError()))
            })
    }


    // Private fun
    private fun Weather.toUiWeather(): UiWeather {
        return UiWeather(
            id = id,
            title = weather,
            description = description,
            iconPath = iconName
        )
    }

    private fun Temperature.toUiTemperature(): UiTemperature {
        return UiTemperature(
            temperature = String.format("%.1f", temperature),
            maxTemperature = String.format("%.1f", maxTemperature),
            minTemperature = String.format("%.1f", minTemperature),
            feelsLike = String.format("%.1f", feelsLike),
            pressure = pressure.toString(),
            humidity = String.format("%.1f", humidity),
            measurementUnit = TemperatureMeasurementUnit.Celsius
        )
    }

    private fun GetValidSearchPatternUseCase.PatternValidationError.toSearchError(): SearchError =
        when (this) {
            GetValidSearchPatternUseCase.PatternValidationError.NullOrEmptyPattern -> SearchError.FieldCannotBeNull
            GetValidSearchPatternUseCase.PatternValidationError.TooManyCommaParams -> SearchError.Only3ParamsAreAllowed
            GetValidSearchPatternUseCase.PatternValidationError.NoParamsFound -> SearchError.PleaseInsertTheCity
        }

    private fun SafeRequestError.toSearchError(searchedPattern: String): SearchError {
        return when (this) {
            SafeRequestError.Generic -> SearchError.Generic
            SafeRequestError.NetworkError -> SearchError.NoInternet
            SafeRequestError.NotFound -> SearchError.WeatherNotFound(searchedPattern)
        }
    }
}