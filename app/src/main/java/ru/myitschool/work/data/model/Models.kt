package ru.myitschool.work.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String,
    val avatar: String? = null,
    val bookings: List<Booking> = emptyList()
)

@Serializable
data class Booking(
    val date: String,
    val place: String
)

@Serializable
data class DayAvailability(
    val date: String,
    val places: List<String>
)

@Serializable
data class BookingRequest(
    val date: String,
    val place: String
)
