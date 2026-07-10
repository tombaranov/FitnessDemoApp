package tombaranov.fitnessdemoapp.core.network

sealed class Response<out T> {
    data class Success<T>(val data: T) : Response<T>()

    sealed class Failure : Response<Nothing>() {
        object BadRequest : Failure()
        object Unauthorized : Failure()
        object Forbidden : Failure()
        object NotFound : Failure()
        data class TooManyRequests(val retryAfter: Int?) : Failure()
        object ClosedRequest : Failure()

        object InternalServerError : Failure()
        object ConnectTimeout : Failure()

        object SocketTimeout : Failure()
        object ConnectException : Failure()
        object UnknownHostException : Failure()

        data class HttpError(
            val httpCode: Int,
            val responseBodyString: String?,
        ) : Failure()

        data class Another(val throwable: Throwable) : Failure()

        val isClientError: Boolean
            get() = when (this) {
                BadRequest,
                Unauthorized,
                Forbidden,
                NotFound,
                is TooManyRequests,
                ClosedRequest -> true

                is HttpError -> httpCode in ErrorCode.http400ErrorRange
                else -> false
            }
    }
}


