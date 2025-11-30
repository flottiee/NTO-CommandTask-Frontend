package ru.myitschool.work.data.repo

import ru.myitschool.work.data.model.BookingInfo
import ru.myitschool.work.data.source.NetworkDataSource

object BookRepository {
    suspend fun getAvailableBookings(): Result<Map<String, List<BookingInfo>>> {
        val code = AuthRepository.getAuthCode() ?: return Result.failure(Exception("No auth code"))
        return NetworkDataSource.getAvailableBookings(code)
    }

    suspend fun bookPlace(date: String, place: String): Result<Unit> {
        val code = AuthRepository.getAuthCode() ?: return Result.failure(Exception("No auth code"))
        return NetworkDataSource.bookPlace(code, date, place)
    }
}