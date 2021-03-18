package klodian.kambo.data.utils

import arrow.core.Either
import klodian.kambo.domain.model.SafeRequestError
import retrofit2.HttpException
import java.io.IOException

suspend fun <T : Any> performSafeRequest(requestFunc: suspend () -> T): Either<SafeRequestError, T> {
    return try {
        Either.right(requestFunc.invoke())
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> Either.left(SafeRequestError.NetworkError)
            is HttpException -> {
                // Can do further mapping
                when (throwable.code()) {
                    404 -> Either.left(SafeRequestError.NotFound)
                    else -> Either.left(SafeRequestError.Generic)
                }
            }
            else -> Either.left(SafeRequestError.Generic)
        }
    }
}