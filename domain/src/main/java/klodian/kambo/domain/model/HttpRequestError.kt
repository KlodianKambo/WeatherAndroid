package klodian.kambo.domain.model

sealed class HttpRequestError{
    object Generic : HttpRequestError()
    object NetworkError : HttpRequestError()
    object NotFound : HttpRequestError()
}