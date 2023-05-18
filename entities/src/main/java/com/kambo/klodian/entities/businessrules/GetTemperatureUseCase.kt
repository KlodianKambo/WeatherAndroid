package com.kambo.klodian.entities.businessrules

import com.kambo.klodian.entities.model.TemperatureUnit
import javax.inject.Inject

class GetTemperatureUseCase @Inject constructor() {
    operator fun invoke(
        temperature: Double,
        inMeasurementUnit: TemperatureUnit
    ): Double {
        return when (inMeasurementUnit) {
            TemperatureUnit.Fahrenheit -> (temperature * 1.8) + 32
            TemperatureUnit.Celsius -> (temperature - 32) / 1.8
        }
    }
}