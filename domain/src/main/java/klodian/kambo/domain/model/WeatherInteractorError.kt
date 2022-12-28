package klodian.kambo.domain.model

sealed class WeatherInteractorError {
    data class HttpError(val error: HttpRequestError): WeatherInteractorError()
}

sealed class PatternValidationError: WeatherInteractorError(){
    object NullOrEmptyPattern : PatternValidationError()
    object TooManyCommaParams : PatternValidationError()
    object NoParamsFound : PatternValidationError()
}