package ru.myitschool.work.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String,
    @SerialName("photoUrl")
    val avatar: String? = null,
    @SerialName("booking")
    val bookings: Map<String, BookingInfo> = emptyMap(),
)

data class Booking(
    val date: String,
    val id: Long,
    val place: String
)

@Serializable
data class BookingInfo(
    val id: Long,
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
