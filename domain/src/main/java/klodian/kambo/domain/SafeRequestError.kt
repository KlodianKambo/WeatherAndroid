package klodian.kambo.domain

sealed class SafeRequestError{
    object Generic : SafeRequestError()
    object NetworkError : SafeRequestError()
    object NotFound : SafeRequestError()
}