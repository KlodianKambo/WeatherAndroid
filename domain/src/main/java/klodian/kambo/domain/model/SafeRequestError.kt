package klodian.kambo.domain.model

sealed class SafeRequestError{
    object Generic : SafeRequestError()
    object NetworkError : SafeRequestError()
    object NotFound : SafeRequestError()
}