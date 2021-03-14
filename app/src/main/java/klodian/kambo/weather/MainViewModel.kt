package klodian.kambo.weather

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import klodian.kambo.domain.GetValidSearchPatternUseCase
import klodian.kambo.domain.SafeRequestError
import klodian.kambo.domain.Weather
import klodian.kambo.domain.WeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo,
    private val getValidSearchPatternUseCase: GetValidSearchPatternUseCase
) : ViewModel() {

    sealed class SearchError(@StringRes val errorMessageResId: Int) {
        object FieldCannotBeNull : SearchError(R.string.search_input_error_empty)
        object Only3ParamsAreAllowed : SearchError(R.string.search_input_error_too_many_params)
        object PleaseInsertTheCity : SearchError(R.string.search_input_error_no_param_found)

        object NoInternet : SearchError(R.string.search_error_no_internet)
        data class WeatherNotFound(val searchValue: String) : SearchError(R.string.search_error_not_found)

        object Generic : SearchError(R.string.search_error_generic)
    }

    private val weatherLiveData: MutableLiveData<Either<SearchError, List<UiWeather>>> =
        MutableLiveData()

    fun getWeatherResult(): LiveData<Either<SearchError, List<UiWeather>>> = weatherLiveData

    fun getWeather(pattern: String, locale: Locale) {
        getValidSearchPatternUseCase(pattern).fold(
            ifRight = { correctedPattern ->
                viewModelScope.launch(Dispatchers.IO) {
                    weatherRepo.getWeather(correctedPattern, locale).fold(
                        ifLeft = { safeRequestError ->
                            Either.left(safeRequestError.toSearchError(correctedPattern))
                        },
                        ifRight = { weatherList ->
                            weatherLiveData.postValue(
                                Either.right(
                                    weatherList.map { weather -> weather.toUiWeather() })
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

    private fun GetValidSearchPatternUseCase.PatternValidationError.toSearchError(): SearchError =
        when (this) {
            GetValidSearchPatternUseCase.PatternValidationError.NullOrEmptyPattern -> SearchError.FieldCannotBeNull
            GetValidSearchPatternUseCase.PatternValidationError.TooManyCommaParams -> SearchError.Only3ParamsAreAllowed
            GetValidSearchPatternUseCase.PatternValidationError.NoParamsFound -> SearchError.PleaseInsertTheCity
        }

    private fun SafeRequestError.toSearchError(searchedPattern: String) {
        when (this) {
            SafeRequestError.Generic -> SearchError.Generic
            SafeRequestError.NetworkError -> SearchError.NoInternet
            SafeRequestError.NotFound -> SearchError.WeatherNotFound(searchedPattern)
        }
    }
}