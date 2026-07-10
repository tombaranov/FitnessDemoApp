package tombaranov.fitnessdemoapp.core.network

object ErrorCode {
    const val BAD_REQUEST = 400

    const val UNAUTHORIZED = 401

    const val FORBIDDEN = 403

    const val NOT_FOUND = 404

    const val TOO_MANY_REQUESTS = 429

    const val CLOSED_REQUEST = 499
    const val INTERNAL_SERVER_ERROR = 500
    const val CONNECT_TIMEOUT = 599


    val http400ErrorRange = BAD_REQUEST..CLOSED_REQUEST
    val http500ErrorRange = INTERNAL_SERVER_ERROR..CONNECT_TIMEOUT
}
