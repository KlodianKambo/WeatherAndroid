package com.kambo.klodian.ui.ui.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.kambo.klodian.entities.model.*
import com.kambo.klodian.ui.R
import com.kambo.klodian.ui.ui.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import klodian.kambo.domain.model.HttpRequestError
import klodian.kambo.domain.model.PatternValidationError
import klodian.kambo.domain.usecases.GetValidSearchPatternUseCase
import klodian.kambo.domain.usecases.GetWeatherByCityNameUseCase
import klodian.kambo.domain.usecases.GetWeatherByLocationUseCase
import klodian.kambo.domain.usecases.location.GetLocationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherByCityNameUseCase,
    private val getWeatherByLocationUseCase: GetWeatherByLocationUseCase,
    private val getValidSearchPatternUseCase: GetValidSearchPatternUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getTemperatureUseCase: com.kambo.klodian.entities.businessrules.GetTemperatureUseCase
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    private val dateFormatterForTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    private val uiWeatherInfoFlow =
        MutableStateFlow<Either<SearchError, UiCompleteWeatherInfo?>>(Either.right(null))

    private val measurementUnitFlow = MutableStateFlow<TemperatureUnit>(TemperatureUnit.Celsius)
    private val welcomeEnabledFlow = MutableStateFlow(true)
    private val isLoadingFlow = MutableStateFlow(false)

    fun getWeatherResult(): Flow<Either<SearchError, UiCompleteWeatherInfo?>> = uiWeatherInfoFlow
    fun isLoading(): Flow<Boolean> = isLoadingFlow
    fun isWelcomeEnabled(): Flow<Boolean> = welcomeEnabledFlow

    fun toggleTemperature() {

        viewModelScope.launch {
            val newTempUnit = when (measurementUnitFlow.value) {
                TemperatureUnit.Fahrenheit -> TemperatureUnit.Celsius
                TemperatureUnit.Celsius -> TemperatureUnit.Fahrenheit
            }

            measurementUnitFlow.tryEmit(newTempUnit)

            uiWeatherInfoFlow.value.orNull()?.let { uiCompleteWeatherInfo ->
                uiWeatherInfoFlow.tryEmit(
                    Either.right(getUpdateTemperature(uiCompleteWeatherInfo, newTempUnit))
                )
            }
        }
    }

    fun getTemperature(): Flow<UiTemperatureMeasurementUnit> {
        return measurementUnitFlow.map {
            when (it) {
                TemperatureUnit.Fahrenheit -> UiTemperatureMeasurementUnit.Celsius
                TemperatureUnit.Celsius -> UiTemperatureMeasurementUnit.Fahrenheit
            }
        }
    }

    fun getWeather(pattern: String, locale: Locale) {
        getValidSearchPatternUseCase(pattern).fold(
            ifRight = { correctedPattern ->

                isLoadingFlow.tryEmit(true)
                welcomeEnabledFlow.tryEmit(false)

                viewModelScope.launch(Dispatchers.IO) {
                    val measurementUnit = measurementUnitFlow.value

                    getWeatherUseCase(
                        cityName = correctedPattern,
                        locale = locale,
                        measurementUnit = measurementUnit,
                        zoneId = ZoneId.systemDefault()
                    ).fold(
                        ifLeft = { safeRequestError ->
                            isLoadingFlow.tryEmit(false)
                            uiWeatherInfoFlow.tryEmit(
                                Either.left(
                                    safeRequestError.toSearchError(correctedPattern)
                                )
                            )
                        },
                        ifRight = { forecastWeather ->
                            val temperatureUnit =
                                measurementUnitFlow.value ?: TemperatureUnit.Celsius
                            isLoadingFlow.tryEmit(false)
                            uiWeatherInfoFlow.tryEmit(
                                Either.right(forecastWeather.toUiCompleteWeatherInfo(temperatureUnit))
                            )
                        })
                }
            },
            ifLeft = { patternValidationError ->
                uiWeatherInfoFlow.tryEmit(Either.left(patternValidationError.toSearchError()))
            })
    }

    fun fetchByCurrentLocation(locale: Locale){
        isLoadingFlow.tryEmit(true)
        welcomeEnabledFlow.tryEmit(false)

        viewModelScope.launch(Dispatchers.IO) {
            val measurementUnit = measurementUnitFlow.value
            getLocationUseCase()
                .fold(
                    ifLeft = {
                        isLoadingFlow.tryEmit(false)
                        uiWeatherInfoFlow.tryEmit(
                            Either.left(SearchError.PermissionsDenied)
                        )
                    },
                    ifRight = {
                        getWeatherByLocationUseCase(
                            latitude = it.latitude,
                            longitude = it.longitude,
                            locale = locale,
                            measurementUnit = measurementUnit,
                            zoneId = ZoneId.systemDefault()
                        ).fold(
                            ifLeft = { safeRequestError ->
                                isLoadingFlow.tryEmit(false)

                                uiWeatherInfoFlow.tryEmit(
                                    Either.left(safeRequestError.toSearchError(""))
                                )

                            },
                            ifRight = { forecastWeather ->
                                val temperatureUnit = measurementUnitFlow.value
                                isLoadingFlow.tryEmit(false)
                                uiWeatherInfoFlow.tryEmit(
                                    Either.right(
                                        forecastWeather.toUiCompleteWeatherInfo(temperatureUnit)
                                    )
                                )
                            })
                    }
                )
        }
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

    private fun Temperature.toUiTemperature(temperatureUnit: TemperatureUnit): UiTemperature {
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

    private fun UiTemperature.convertTo(temperatureUnit: TemperatureUnit): UiTemperature {
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

    private fun CompleteWeatherInfo.toUiWeatherTemperature(temperatureUnit: TemperatureUnit): UiWeatherTemperature {
        return UiWeatherTemperature(
            displayableHour = dateFormatterForTime.format(date),
            id = UUID.randomUUID().toString(),
            weather = weather.first().toUiWeather(),
            temperature = temperature.toUiTemperature(temperatureUnit)
        )
    }


    private fun PatternValidationError.toSearchError(): SearchError =
        when (this) {
            PatternValidationError.NullOrEmptyPattern -> SearchError.FieldCannotBeNull
            PatternValidationError.TooManyCommaParams -> SearchError.Only3ParamsAreAllowed
            PatternValidationError.NoParamsFound -> SearchError.PleaseInsertTheCity
        }

    private fun HttpRequestError.toSearchError(searchedPattern: String): SearchError {
        return when (this) {
            HttpRequestError.Generic -> SearchError.Generic
            HttpRequestError.NetworkError -> SearchError.NoInternet
            HttpRequestError.NotFound -> SearchError.WeatherNotFound(
                searchedPattern
            )
        }
    }

    private fun Double.formatToOneDecimalTemperature(temperatureUnit: TemperatureUnit): String {
        return String.format("%.1f°${temperatureUnit.symbol}", this)
    }

    private fun getUpdateTemperature(
        uiCompleteWeatherInfo: UiCompleteWeatherInfo,
        temperatureUnit: TemperatureUnit
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
        temperatureUnit: TemperatureUnit
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


    object PermissionsDenied :
        SearchError(R.string.search_error_permission_denied, R.drawable.ic_baseline_error_outline)

    object Generic :
        SearchError(R.string.search_error_generic, R.drawable.ic_baseline_error_outline)
}