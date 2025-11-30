package ru.myitschool.work.data.source

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.myitschool.work.core.Constants
import ru.myitschool.work.data.model.BookingRequest
import ru.myitschool.work.data.model.DayAvailability
import ru.myitschool.work.data.model.UserInfo

object NetworkDataSource {
    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                        encodeDefaults = true
                    }
                )
            }
        }
    }

    suspend fun checkAuth(code: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = client.get(getUrl(code, Constants.AUTH_URL))
            when (response.status) {
                HttpStatusCode.OK -> true
                else -> error(response.bodyAsText())
            }
        }
    }

    suspend fun getUserInfo(code: String): Result<UserInfo> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = client.get(getUrl(code, Constants.INFO_URL))
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                error(response.bodyAsText())
            }
        }
    }

    suspend fun getAvailableBookings(code: String): Result<List<DayAvailability>> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = client.get(getUrl(code, Constants.BOOKING_URL))
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                error(response.bodyAsText())
            }
        }
    }

    suspend fun bookPlace(code: String, date: String, place: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = client.post(getUrl(code, Constants.BOOK_URL)) {
                contentType(ContentType.Application.Json)
                setBody(BookingRequest(date, place))
            }
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                return@runCatching
            } else {
                error(response.bodyAsText())
            }
        }
    }

    private fun getUrl(code: String, targetUrl: String) = "${Constants.HOST}/api/$code$targetUrl"
}