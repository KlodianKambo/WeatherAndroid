package klodian.kambo.data

import javax.inject.Inject

class GetValidSearchPatternUseCase @Inject constructor() {

    sealed class PatternValidationError {
        object NullOrEmptyPattern : PatternValidationError()
    }

    operator fun invoke(
        pattern: String?,
        success: (validPattern: String) -> Unit,
        error: (patternValidationError: PatternValidationError) -> Unit
    ) {
        if (pattern.isNullOrEmpty()) {
            error(PatternValidationError.NullOrEmptyPattern)
        } else {
            success(
                pattern
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .joinToString(separator = ",")
            )
        }
    }
}