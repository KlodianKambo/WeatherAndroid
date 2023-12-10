package klodian.kambo.data.utils

import arrow.core.Either
import klodian.kambo.domain.model.HttpRequestError
import retrofit2.HttpException
import java.io.IOException

internal suspend fun <T : Any> performCatchingRequest(requestFunc: suspend () -> T): Either<HttpRequestError, T> {
    return try {
        Either.right(requestFunc.invoke())
    } catch (io: IOException) {
        Either.left(HttpRequestError.NetworkError)
    } catch (http: HttpException) {
        when (http.code()) {
            404 -> Either.left(HttpRequestError.NotFound)
            else -> Either.left(HttpRequestError.Generic)
        }
    }
}

