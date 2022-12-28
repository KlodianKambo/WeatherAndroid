package klodian.kambo.data

import arrow.core.Either
import klodian.kambo.data.utils.performSafeRequest
import klodian.kambo.domain.model.HttpRequestError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class SafeRequestTest {

    @Test
    fun `when lambda returns successfully then emit the result as success`() {
        runBlockingTest {
            val expectedResult = true
            val result = performSafeRequest { expectedResult }
            assertEquals(Either.right(expectedResult), result)
        }
    }

    @Test
    fun `when lambda throws IOException then emit NetworkError`() {
        runBlockingTest {
            val result = performSafeRequest { throw IOException() }
            assertEquals(Either.left(HttpRequestError.NetworkError), result)
        }
    }

    @Test
    fun `when lambda throws HttpException as Not Found then emit NotFound`() {
        val errorBody = ResponseBody.create(
            MediaType.parse("application/json"),
            "{\"errors\": [\"404 Not found\"]}"
        )

        runBlockingTest {
            val result =
                performSafeRequest { throw HttpException(Response.error<Any>(404, errorBody)) }
            assertEquals(Either.left(HttpRequestError.NotFound), result)
        }
    }

    @Test
    fun `when lambda throws HttpException different from Not Found then emit Generic`() {
        val errorBody = ResponseBody.create(
            MediaType.parse("application/json"),
            "{\"errors\": [\"402 Payment Required\"]}"
        )
        runBlockingTest {
            val result =
                performSafeRequest { throw HttpException(Response.error<Any>(402, errorBody)) }
            assertEquals(Either.left(HttpRequestError.Generic), result)
        }
    }

    @Test
    fun `when lambda throws unknown exception then it should emit GenericError`() {
        runBlockingTest {
            val result = performSafeRequest { throw IllegalStateException() }
            assertEquals(Either.left(HttpRequestError.Generic), result)
        }
    }
}