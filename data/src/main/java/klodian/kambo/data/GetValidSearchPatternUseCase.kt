package klodian.kambo.data

import javax.inject.Inject

class GetValidSearchPatternUseCase @Inject constructor() {

    sealed class PatternValidationError {
        object NullOrEmptyPattern : PatternValidationError()
        object TooManyCommaParams : PatternValidationError()
        object NoParamsFound : PatternValidationError()
    }

    operator fun invoke(
        pattern: String?,
        success: (validPattern: String) -> Unit,
        error: (patternValidationError: PatternValidationError) -> Unit
    ) {
        if (pattern.isNullOrEmpty()) {
            error(PatternValidationError.NullOrEmptyPattern)
        } else {

            val meaningfulCommaParams = pattern
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (meaningfulCommaParams.isEmpty()) {
                error(PatternValidationError.NoParamsFound)
                return
            }

            if (meaningfulCommaParams.size > 3) {
                error(PatternValidationError.TooManyCommaParams)
                return
            }

            success(meaningfulCommaParams.joinToString(separator = ","))
        }
    }
}