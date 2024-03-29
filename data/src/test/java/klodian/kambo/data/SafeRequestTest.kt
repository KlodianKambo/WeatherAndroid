package klodian.kambo.data

import arrow.core.Either
import klodian.kambo.data.utils.performCatchingRequest
import klodian.kambo.domain.model.HttpRequestError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            val expectedResult = true
            val result = performCatchingRequest { expectedResult }
            assertEquals(Either.right(expectedResult), result)
        }
    }

    @Test
    fun `when lambda throws IOException then emit NetworkError`() {
        runBlocking {
            val result = performCatchingRequest { throw IOException() }
            assertEquals(Either.left(HttpRequestError.NetworkError), result)
        }
    }

    @Test
    fun `when lambda throws HttpException as Not Found then emit NotFound`() {
        val errorBody = ResponseBody.create(
            MediaType.parse("application/json"),
            "{\"errors\": [\"404 Not found\"]}"
        )

        runBlocking {
            val result =
                performCatchingRequest { throw HttpException(Response.error<Any>(404, errorBody)) }
            assertEquals(Either.left(HttpRequestError.NotFound), result)
        }
    }

    @Test
    fun `when lambda throws HttpException different from Not Found then emit Generic`() {
        val errorBody = ResponseBody.create(
            MediaType.parse("application/json"),
            "{\"errors\": [\"402 Payment Required\"]}"
        )
        runBlocking {
            val result =
                performCatchingRequest { throw HttpException(Response.error<Any>(402, errorBody)) }
            assertEquals(Either.left(HttpRequestError.Generic), result)
        }
    }

    @Test
    fun `when lambda throws unknown exception then it should emit GenericError`() {
        runBlocking {
            val result = performCatchingRequest { throw IllegalStateException() }
            assertEquals(Either.left(HttpRequestError.Generic), result)
        }
    }
}