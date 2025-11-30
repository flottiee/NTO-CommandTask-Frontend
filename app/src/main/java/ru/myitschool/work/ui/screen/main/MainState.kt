package ru.myitschool.work.ui.screen.main

import ru.myitschool.work.data.model.Booking
import ru.myitschool.work.data.model.BookingInfo

sealed interface MainState {
    object Loading : MainState
    data class Data(
        val name: String,
        val avatar: String?,
        val bookings: List<Booking>
    ) : MainState
    object Error : MainState
}