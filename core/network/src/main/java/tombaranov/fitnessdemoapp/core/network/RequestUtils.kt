package tombaranov.fitnessdemoapp.core.network

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

inline fun <reified T> handleRequest(request: () -> T): Response<T> {
    return try {
        val data = request()

        Response.Success(data = data)
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        val errorCode = e.code()

        when (errorCode) {
            ErrorCode.BAD_REQUEST -> Response.Failure.BadRequest
            ErrorCode.UNAUTHORIZED -> Response.Failure.Unauthorized
            ErrorCode.FORBIDDEN -> Response.Failure.Forbidden
            ErrorCode.NOT_FOUND -> Response.Failure.NotFound
            ErrorCode.TOO_MANY_REQUESTS -> Response.Failure.TooManyRequests(
                e.response()?.headers()?.get(NetworkHeaders.RETRY_AFTER.header)?.toIntOrNull()
            )
            ErrorCode.CLOSED_REQUEST -> Response.Failure.ClosedRequest

            ErrorCode.INTERNAL_SERVER_ERROR -> Response.Failure.InternalServerError
            ErrorCode.CONNECT_TIMEOUT -> Response.Failure.ConnectTimeout

            else -> Response.Failure.HttpError(
                httpCode = e.code(),
                responseBodyString = e.response()?.errorBody()?.string()
            )
        }
    } catch (_: SocketTimeoutException) {
        Response.Failure.SocketTimeout
    } catch (_: ConnectException) {
        Response.Failure.ConnectException
    } catch (_: UnknownHostException) {
        Response.Failure.UnknownHostException
    } catch (throwable: Throwable) {
        Response.Failure.Another(throwable = throwable)
    }
}
