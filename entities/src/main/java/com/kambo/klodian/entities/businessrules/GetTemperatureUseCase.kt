package com.kambo.klodian.entities.businessrules

import com.kambo.klodian.entities.model.TemperatureMeasurementUnit
import javax.inject.Inject

class GetTemperatureUseCase @Inject constructor() {
    operator fun invoke(
        temperature: Double,
        inMeasurementUnit: TemperatureMeasurementUnit
    ): Double {
        return when (inMeasurementUnit) {
            TemperatureMeasurementUnit.Fahrenheit -> (temperature * 1.8) + 32
            TemperatureMeasurementUnit.Celsius -> (temperature - 32) / 1.8
        }
    }
}