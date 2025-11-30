package ru.myitschool.work.ui.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.data.model.Booking
import ru.myitschool.work.data.repo.MainRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MainState>(MainState.Loading)
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    private val _actionFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val actionFlow: SharedFlow<Unit> = _actionFlow

    init {
        loadData()
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.Refresh -> loadData()
            is MainIntent.Logout -> logout()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { MainState.Loading }
            MainRepository.getUserInfo().fold(
                onSuccess = { userInfo ->
                    Log.d("MainViewModel", "userInfo: $userInfo")
                    val sortedBookings = userInfo.bookings.map {
                        Booking(
                            date = it.key,
                            id = it.value.id,
                            place = it.value.place
                        )
                    }.toList().sortedBy {
                        // Sort by date.
                        // API dates might be ISO (yyyy-MM-dd) or dd.MM.yyyy
                        // Requirements say "dd.MM.yyyy" is for display.
                        // If API returns yyyy-MM-dd, we should parse that.
                        // If API returns dd.MM.yyyy, we parse that.
                        // Models.kt Booking.date is String.
                        // Let's try both or standard ISO first as it's API.
                        // Actually, usually APIs return ISO 8601.
                        // But in problem description: "Текстовое поле ... с датой бронирования в формате dd.MM.yyyy". This refers to UI.
                        // Let's assume standard sorting by string works if it's ISO.
                        // If it's dd.MM.yyyy, string sort is wrong (01.02 vs 02.01).
                        // Let's implement robust parsing.

                        try {
                            if (it.date.contains("-")) {
                                LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).toEpochDay()
                            } else if (it.date.contains(".")) {
                                LocalDate.parse(it.date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                    .toEpochDay()
                            } else {
                                0L
                            }
                        } catch (e: Exception) {
                            0L
                        }
                    }
                    _uiState.update {
                        MainState.Data(
                            name = userInfo.name,
                            avatar = userInfo.avatar,
                            bookings = sortedBookings
                        )
                    }
                },
                onFailure = {
                    _uiState.update { MainState.Error }
                }
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            MainRepository.logout()
            _actionFlow.emit(Unit)
        }
    }
}