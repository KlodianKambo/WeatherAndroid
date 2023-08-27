package klodian.kambo.data.utils

import arrow.core.Either
import klodian.kambo.domain.model.HttpRequestError
import retrofit2.HttpException
import java.io.IOException

internal suspend fun <T : Any> performSafeRequest(requestFunc: suspend () -> T): Either<HttpRequestError, T> {
    return try {
        Either.right(requestFunc.invoke())
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> Either.left(HttpRequestError.NetworkError)
            is HttpException -> {
                // Can do further mapping
                when (throwable.code()) {
                    404 -> Either.left(HttpRequestError.NotFound)
                    else -> Either.left(HttpRequestError.Generic)
                }
            }
            else -> Either.left(HttpRequestError.Generic)
        }
    }
}