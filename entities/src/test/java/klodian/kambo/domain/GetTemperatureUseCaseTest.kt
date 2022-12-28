package klodian.kambo.domain

import com.kambo.klodian.entities.businessrules.GetTemperatureUseCase
import com.kambo.klodian.entities.model.TemperatureMeasurementUnit
import org.junit.Test
import kotlin.test.assertEquals

class GetTemperatureUseCaseTest {

    @Test
    fun test() {
        val useCase = GetTemperatureUseCase()
        assertEquals(32.0, useCase(0.0, TemperatureMeasurementUnit.Fahrenheit))
        assertEquals(0.0, useCase(32.0, TemperatureMeasurementUnit.Celsius))

        assertEquals(-4.0, useCase(-20.0, TemperatureMeasurementUnit.Fahrenheit))
        assertEquals(-20.0, useCase(-4.0, TemperatureMeasurementUnit.Celsius))

        assertEquals(41.0, useCase(5.0, TemperatureMeasurementUnit.Fahrenheit))
        assertEquals(5.0, useCase(41.0, TemperatureMeasurementUnit.Celsius))
    }
}