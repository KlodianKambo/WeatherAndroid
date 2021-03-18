package klodian.kambo.weather.ui.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import arrow.core.Either
import klodian.kambo.domain.model.*
import klodian.kambo.domain.repositories.WeatherRepo
import klodian.kambo.domain.usecases.GetTemperatureUseCase
import klodian.kambo.domain.usecases.GetValidSearchPatternUseCase
import klodian.kambo.weather.R
import klodian.kambo.weather.ui.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo,
    private val getValidSearchPatternUseCase: GetValidSearchPatternUseCase,
    private val getTemperatureUseCase: GetTemperatureUseCase
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

    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    private val dateFormatterForTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    private val weatherLiveData: MutableLiveData<Either<SearchError, UiCompleteWeatherInfo>> =
        MutableLiveData()

    private val welcomeEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    private val measurementUnitLiveData: MutableLiveData<TemperatureMeasurementUnit> =
        MutableLiveData(TemperatureMeasurementUnit.Celsius)

    private val loadingLiveData = MutableLiveData(false)

    fun getWeatherResult(): LiveData<Either<SearchError, UiCompleteWeatherInfo>> = weatherLiveData
    fun isLoading(): LiveData<Boolean> = loadingLiveData
    fun isWelcomeEnabled(): LiveData<Boolean> = welcomeEnabled

    fun toggleTemperature() {

        val newTempUnit = when (measurementUnitLiveData.value) {
            TemperatureMeasurementUnit.Fahrenheit -> TemperatureMeasurementUnit.Celsius
            TemperatureMeasurementUnit.Celsius -> TemperatureMeasurementUnit.Fahrenheit
            null -> TemperatureMeasurementUnit.Celsius
        }

        measurementUnitLiveData.value = newTempUnit

        weatherLiveData.value?.let {
            it.orNull()?.let { uiCompleteWeatherInfo ->
                weatherLiveData.postValue(
                    Either.right(getUpdateTemperature(uiCompleteWeatherInfo, newTempUnit))
                )
            }
        }
    }

    fun getTemperature(): LiveData<UiTemperatureMeasurementUnit> {
        return measurementUnitLiveData.switchMap {
            val newUiTempUnit = when (measurementUnitLiveData.value) {
                TemperatureMeasurementUnit.Fahrenheit -> UiTemperatureMeasurementUnit.Celsius
                TemperatureMeasurementUnit.Celsius -> UiTemperatureMeasurementUnit.Fahrenheit
                null -> UiTemperatureMeasurementUnit.Celsius
            }
            MutableLiveData(newUiTempUnit)
        }
    }

    fun getWeather(pattern: String, locale: Locale) {
        getValidSearchPatternUseCase(pattern).fold(
            ifRight = { correctedPattern ->
                loadingLiveData.postValue(true)
                welcomeEnabled.postValue(false)
                viewModelScope.launch(Dispatchers.IO) {
                    val measurementUnit =
                        measurementUnitLiveData.value ?: TemperatureMeasurementUnit.Celsius

                    weatherRepo.getWeather(
                        cityName = correctedPattern,
                        locale = locale,
                        measurementUnit = measurementUnit,
                        zoneId = ZoneId.systemDefault()
                    ).fold(
                        ifLeft = { safeRequestError ->
                            loadingLiveData.postValue(false)
                            weatherLiveData.postValue(
                                Either.left(
                                    safeRequestError.toSearchError(correctedPattern)
                                )
                            )
                        },
                        ifRight = { forecastWeather ->
                            val temperatureUnit =
                                measurementUnitLiveData.value ?: TemperatureMeasurementUnit.Celsius
                            loadingLiveData.postValue(false)
                            weatherLiveData.postValue(
                                Either.right(forecastWeather.toUiCompleteWeatherInfo(temperatureUnit))
                            )
                        })
                }
            },
            ifLeft = { patternValidationError ->
                weatherLiveData.postValue(Either.left(patternValidationError.toSearchError()))
            })
    }


    // Private fun *********************************************************************************
    private fun Weather.toUiWeather(): UiWeather {
        return UiWeather(
            id = id,
            title = weather,
            description = description,
            iconPath = iconName
        )
    }

    private fun Temperature.toUiTemperature(temperatureUnit: TemperatureMeasurementUnit): UiTemperature {
        return UiTemperature(
            displayableTemperature = temperature.formatToOneDecimalTemperature(temperatureUnit),
            displayableMaxTemperature = maxTemperature.formatToOneDecimalTemperature(temperatureUnit),
            displayableMinTemperature = minTemperature.formatToOneDecimalTemperature(temperatureUnit),
            displayableFeelsLike = feelsLike.formatToOneDecimalTemperature(temperatureUnit),
            temperature = temperature,
            feelsLike = feelsLike,
            minTemperature = minTemperature,
            maxTemperature = maxTemperature,
            pressure = pressure.toString(),
            humidity = String.format("%.1f%%", humidity)
        )
    }

    private fun UiTemperature.convertTo(temperatureUnit: TemperatureMeasurementUnit): UiTemperature {
        val newTemp = getTemperatureUseCase(temperature, temperatureUnit)
        val newMaxTemp = getTemperatureUseCase(maxTemperature, temperatureUnit)
        val newMinTemp = getTemperatureUseCase(minTemperature, temperatureUnit)
        val newFeelsLikeTemp = getTemperatureUseCase(feelsLike, temperatureUnit)

        return this.copy(
            displayableTemperature = newTemp.formatToOneDecimalTemperature(temperatureUnit),
            displayableMaxTemperature = newMaxTemp.formatToOneDecimalTemperature(temperatureUnit),
            displayableMinTemperature = newMinTemp.formatToOneDecimalTemperature(temperatureUnit),
            displayableFeelsLike = newFeelsLikeTemp.formatToOneDecimalTemperature(temperatureUnit),
            temperature = newTemp,
            maxTemperature = newMaxTemp,
            minTemperature = newMinTemp,
            feelsLike = newFeelsLikeTemp
        )
    }

    private fun CompleteWeatherInfo.toUiWeatherTemperature(temperatureUnit: TemperatureMeasurementUnit): UiWeatherTemperature {
        return UiWeatherTemperature(
            displayableHour = dateFormatterForTime.format(date),
            id = UUID.randomUUID().toString(),
            weather = weather.first().toUiWeather(),
            temperature = temperature.toUiTemperature(temperatureUnit)
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

    private fun Double.formatToOneDecimalTemperature(temperatureUnit: TemperatureMeasurementUnit): String {
        return String.format("%.1f°${temperatureUnit.symbol}", this)
    }

    private fun getUpdateTemperature(
        uiCompleteWeatherInfo: UiCompleteWeatherInfo,
        temperatureUnit: TemperatureMeasurementUnit
    ): UiCompleteWeatherInfo {
        return uiCompleteWeatherInfo.copy(
            uiDateWeather = uiCompleteWeatherInfo.uiDateWeather.map { uiWeatherTemperature ->
                uiWeatherTemperature.copy(
                    uiWeatherTemperatureList = uiWeatherTemperature.uiWeatherTemperatureList.map {
                        it.copy(temperature = it.temperature.convertTo(temperatureUnit))
                    }
                )
            }
        )
    }

    private fun ForecastWeather.toUiCompleteWeatherInfo(
        temperatureUnit: TemperatureMeasurementUnit
    ): UiCompleteWeatherInfo {
        val dateWeatherList = completeWeatherInfoList
            .groupBy { it.date.dayOfYear }
            .entries.map {
                UiDateWeather(
                    displayableDay = dateFormatter.format(it.value.first().date),
                    uiWeatherTemperatureList = it.value.map { completeWeatherInfo ->
                        completeWeatherInfo.toUiWeatherTemperature(temperatureUnit)
                    }
                )
            }

        return UiCompleteWeatherInfo(
            uiDateWeather = dateWeatherList,
            cityNameResult = "${city}, $country",
            displayableTimeStamp = LocalDateTime.now().format(dateFormatter)
        )
    }
}