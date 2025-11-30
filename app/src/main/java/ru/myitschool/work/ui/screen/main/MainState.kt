package ru.myitschool.work.ui.screen.main

import android.os.Message
import ru.myitschool.work.data.model.Booking
import ru.myitschool.work.data.model.BookingInfo

sealed interface MainState {
    object Loading : MainState
    data class Data(
        val name: String,
        val avatar: String?,
        val bookings: List<Booking>
    ) : MainState
    data class Error(val message: String) : MainState
}