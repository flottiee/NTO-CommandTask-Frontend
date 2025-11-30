package ru.myitschool.work.ui.screen.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.data.model.DayAvailability
import ru.myitschool.work.data.repo.BookRepository

sealed interface BookState {
    object Loading : BookState
    data class Data(
        val availableDays: List<DayAvailability>,
        val selectedDateIndex: Int = 0,
        val selectedPlace: String? = null
    ) : BookState
    data class Error(val message: String? = null) : BookState
    object Empty : BookState
}

sealed interface BookIntent {
    object Refresh : BookIntent
    data class SelectDate(val index: Int) : BookIntent
    data class SelectPlace(val place: String) : BookIntent
    object Book : BookIntent
}

class BookViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<BookState>(BookState.Loading)
    val uiState: StateFlow<BookState> = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<Unit>()
    val actionFlow: SharedFlow<Unit> = _actionFlow

    init {
        loadData()
    }

    fun onIntent(intent: BookIntent) {
        when (intent) {
            is BookIntent.Refresh -> loadData()
            is BookIntent.SelectDate -> selectDate(intent.index)
            is BookIntent.SelectPlace -> selectPlace(intent.place)
            is BookIntent.Book -> bookPlace()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { BookState.Loading }
            BookRepository.getAvailableBookings().fold(
                onSuccess = { bookings ->
                    val days = bookings.map { DayAvailability(
                        date = it.key,
                        places = it.value
                    ) }
                    val validDays = days.filter { it.places.isNotEmpty() }.sortedBy {
                        try {
                            val parts = it.date.split(".")
                             if (parts.size == 3) {
                                 parts[2].toInt() * 10000 + parts[1].toInt() * 100 + parts[0].toInt()
                             } else {
                                 0
                             }
                        } catch (e: Exception) {
                            0
                        }
                    }

                    if (validDays.isEmpty()) {
                        _uiState.update { BookState.Empty }
                    } else {
                        _uiState.update { 
                            BookState.Data(
                                availableDays = validDays,
                                selectedDateIndex = 0,
                                selectedPlace = null
                            ) 
                        }
                    }
                },
                onFailure = { e ->
                    _uiState.update { BookState.Error(e.message) }
                }
            )
        }
    }

    private fun selectDate(index: Int) {
        _uiState.update { currentState ->
            if (currentState is BookState.Data) {
                currentState.copy(selectedDateIndex = index, selectedPlace = null)
            } else {
                currentState
            }
        }
    }

    private fun selectPlace(place: String) {
        _uiState.update { currentState ->
            if (currentState is BookState.Data) {
                currentState.copy(selectedPlace = place)
            } else {
                currentState
            }
        }
    }

    private fun bookPlace() {
        val currentState = _uiState.value
        if (currentState is BookState.Data) {
            val date = currentState.availableDays[currentState.selectedDateIndex].date
            val place = currentState.selectedPlace ?: return

            viewModelScope.launch {
                _uiState.update { BookState.Loading }
                BookRepository.bookPlace(date, place).fold(
                    onSuccess = {
                        _actionFlow.emit(Unit)
                    },
                    onFailure = { e ->
                        _uiState.update { BookState.Error(e.message) }
                    }
                )
            }
        }
    }
}