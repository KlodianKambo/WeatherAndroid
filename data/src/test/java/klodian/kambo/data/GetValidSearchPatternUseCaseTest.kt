package klodian.kambo.data

import org.junit.Assert
import org.junit.Test


class GetValidSearchPatternUseCaseTest {

    private val getValidSearchPatternUseCase = GetValidSearchPatternUseCase()

    @Test
    fun `when has arbitrary commas and less than 4 params then reduce to meaningful commas`() {
        val patternToTest = ",,, London,  , uk ,"
        val expectedResult = "London,uk"

        getValidSearchPatternUseCase(
            patternToTest,
            success = { result -> Assert.assertEquals(expectedResult, result) },
            error = { throw RuntimeException("Test failed, error $it should not occur") })
    }

    @Test
    fun `when has arbitrary commas and more than 3 params then error occurs`() {
        val patternToTest = ",,, London,  , uk , London, uk,,,"
        val expectedResult = GetValidSearchPatternUseCase.PatternValidationError.TooManyCommaParams

        getValidSearchPatternUseCase(
            patternToTest,
            success = { throw RuntimeException("Test failed, success should not be triggered") },
            error = { patternValidationError ->
                Assert.assertEquals(
                    expectedResult,
                    patternValidationError
                )
            })
    }

    @Test
    fun `when has only commas then error occurs`() {
        val patternToTest = ",,,,,,,,,,,"
        val expectedResult = GetValidSearchPatternUseCase.PatternValidationError.NoParamsFound

        getValidSearchPatternUseCase(
            patternToTest,
            success = { throw RuntimeException("Test failed, success should not be triggered") },
            error = { patternValidationError ->
                Assert.assertEquals(
                    expectedResult,
                    patternValidationError
                )
            })
    }

    @Test
    fun `when is null or empty then NullOrEmpty error occurs`() {
        val nullPatternToTest = null
        val expectedResult = GetValidSearchPatternUseCase.PatternValidationError.NullOrEmptyPattern

        getValidSearchPatternUseCase(
            nullPatternToTest,
            success = { throw RuntimeException("Test failed, success should not be triggered") },
            error = { patternValidationError ->
                Assert.assertEquals(
                    expectedResult,
                    patternValidationError
                )
            })

        val emptyStringToTest = ""

        getValidSearchPatternUseCase(
            emptyStringToTest,
            success = { throw RuntimeException("Test failed, success should not be triggered") },
            error = { patternValidationError ->
                Assert.assertEquals(
                    expectedResult,
                    patternValidationError
                )
            })
    }
}