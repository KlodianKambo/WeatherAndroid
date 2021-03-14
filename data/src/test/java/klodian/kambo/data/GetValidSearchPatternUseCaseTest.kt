package klodian.kambo.data

import org.junit.Assert
import org.junit.Test


class GetValidSearchPatternUseCaseTest {

    @Test
    fun `when has arbitrary commas then reduce to meaningful commas`() {
        val getValidSearchPatternUseCase = GetValidSearchPatternUseCase()

        val patternToTest = ",,, London,  , uk ,"

        val expectedResult = "London,uk"

        getValidSearchPatternUseCase(
            patternToTest,
            success = { result -> Assert.assertEquals(expectedResult, result) },
            error = { throw RuntimeException("Test failed, error $it should not occur") })
    }

    @Test
    fun `when is null or empty then NullOrEmpty error occurs`() {
        val getValidSearchPatternUseCase = GetValidSearchPatternUseCase()

        val nullPatternToTest = null

        val expectedResult = GetValidSearchPatternUseCase.PatternValidationError.NullOrEmptyPattern

        getValidSearchPatternUseCase(
            nullPatternToTest,
            success = { throw RuntimeException("Test failed, result should not be triggered") },
            error = { patternValidationError ->
                Assert.assertEquals(
                    expectedResult,
                    patternValidationError
                )
            })

        val emptyStringToTest = ""

        getValidSearchPatternUseCase(
            emptyStringToTest,
            success = { throw RuntimeException("Test failed, result should not be triggered") },
            error = { patternValidationError ->
                Assert.assertEquals(
                    expectedResult,
                    patternValidationError
                )
            })
    }
}