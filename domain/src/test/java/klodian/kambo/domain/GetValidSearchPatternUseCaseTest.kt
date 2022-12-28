package klodian.kambo.domain

import klodian.kambo.domain.model.PatternValidationError
import klodian.kambo.domain.usecases.GetValidSearchPatternUseCase
import org.junit.Assert.assertEquals
import org.junit.Test


class GetValidSearchPatternUseCaseTest {

    private val getValidSearchPatternUseCase = GetValidSearchPatternUseCase()

    @Test
    fun `when has arbitrary commas and less than 4 params then reduce to meaningful commas`() {
        val patternToTest = ",,, London,  , uk ,"
        val expectedResult = "London,uk"

        getValidSearchPatternUseCase(patternToTest).fold(
            ifRight = { result -> assertEquals(expectedResult, result) },
            ifLeft = { throw RuntimeException("Test failed, error $it should not occur") })
    }

    @Test
    fun `when has arbitrary commas and more than 3 params then error occurs`() {
        val patternToTest = ",,, London,  , uk , London, uk,,,"
        val expectedResult = PatternValidationError.TooManyCommaParams

        getValidSearchPatternUseCase(patternToTest).fold(
            ifRight = { throw RuntimeException("Test failed, success should not be triggered") },
            ifLeft = { patternValidationError ->
                assertEquals(expectedResult, patternValidationError)
            })
    }

    @Test
    fun `when has only commas then error occurs`() {
        val patternToTest = ",,,,,,,,,,,"
        val expectedResult = PatternValidationError.NoParamsFound

        getValidSearchPatternUseCase(patternToTest).fold(
            ifRight = { throw RuntimeException("Test failed, success should not be triggered") },
            ifLeft = { patternValidationError ->
                assertEquals(expectedResult, patternValidationError)
            })
    }

    @Test
    fun `when is null or empty then NullOrEmpty error occurs`() {
        val nullPatternToTest = null
        val expectedResult = PatternValidationError.NullOrEmptyPattern

        getValidSearchPatternUseCase(nullPatternToTest).fold(
            ifRight = { throw RuntimeException("Test failed, success should not be triggered") },
            ifLeft = { patternValidationError ->
                assertEquals(expectedResult, patternValidationError)
            })

        val emptyStringToTest = ""

        getValidSearchPatternUseCase(emptyStringToTest).fold(
            ifRight = { throw RuntimeException("Test failed, success should not be triggered") },
            ifLeft = { patternValidationError ->
                assertEquals(expectedResult, patternValidationError)
            })
    }
}