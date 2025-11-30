package ru.myitschool.work.domain.book

import ru.myitschool.work.data.model.DayAvailability
import ru.myitschool.work.data.repo.BookRepository

class GetAvailableBookingsUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Result<List<DayAvailability>> {
        return repository.getAvailableBookings()
    }
}