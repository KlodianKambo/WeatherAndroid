package klodian.kambo.domain

import com.kambo.klodian.entities.businessrules.GetTemperatureUseCase
import com.kambo.klodian.entities.model.TemperatureUnit
import org.junit.Test
import kotlin.test.assertEquals

class GetTemperatureUseCaseTest {

    @Test
    fun `assert temperature conversion is correct`() {
        val useCase = GetTemperatureUseCase()
        assertEquals(32.0, useCase(0.0, TemperatureUnit.Fahrenheit))
        assertEquals(0.0, useCase(32.0, TemperatureUnit.Celsius))

        assertEquals(-4.0, useCase(-20.0, TemperatureUnit.Fahrenheit))
        assertEquals(-20.0, useCase(-4.0, TemperatureUnit.Celsius))

        assertEquals(41.0, useCase(5.0, TemperatureUnit.Fahrenheit))
        assertEquals(5.0, useCase(41.0, TemperatureUnit.Celsius))
    }
}