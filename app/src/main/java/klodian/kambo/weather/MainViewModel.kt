package klodian.kambo.weather

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import klodian.kambo.domain.GetValidSearchPatternUseCase
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

    sealed class SearchInputError(@StringRes val errorMessageResId: Int) {
        object FieldCannotBeNull : SearchInputError(R.string.search_input_error_empty)
        object Only3ParamsAreAllowed : SearchInputError(R.string.search_input_error_too_many_params)
        object PleaseInsertTheCity : SearchInputError(R.string.search_input_error_no_param_found)
    }

    private val weatherLiveData: MutableLiveData<Either<SearchInputError, List<UiWeather>>> =
        MutableLiveData()

    fun getWeatherResult(): LiveData<Either<SearchInputError, List<UiWeather>>> = weatherLiveData

    fun getWeather(pattern: String, locale: Locale) {
        getValidSearchPatternUseCase(pattern).fold(
            ifRight = { correctedPattern ->
                viewModelScope.launch(Dispatchers.IO) {
                    weatherRepo.getWeather(correctedPattern, locale).fold(
                        ifLeft = {
                            // TODO handle user feedback
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
                weatherLiveData.postValue(Either.left(patternValidationError.toSearchInputError()))
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

    private fun GetValidSearchPatternUseCase.PatternValidationError.toSearchInputError(): SearchInputError =
        when (this) {
            GetValidSearchPatternUseCase.PatternValidationError.NullOrEmptyPattern -> SearchInputError.FieldCannotBeNull
            GetValidSearchPatternUseCase.PatternValidationError.TooManyCommaParams -> SearchInputError.Only3ParamsAreAllowed
            GetValidSearchPatternUseCase.PatternValidationError.NoParamsFound -> SearchInputError.PleaseInsertTheCity
        }
}