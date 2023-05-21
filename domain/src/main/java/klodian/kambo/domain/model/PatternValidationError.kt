package klodian.kambo.domain.model


sealed class PatternValidationError {
    object NullOrEmptyPattern : PatternValidationError()
    object TooManyCommaParams : PatternValidationError()
    object NoParamsFound : PatternValidationError()
}