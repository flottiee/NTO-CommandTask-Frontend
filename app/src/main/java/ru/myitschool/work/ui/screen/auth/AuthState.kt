package ru.myitschool.work.ui.screen.auth

sealed interface AuthState {
    object Loading: AuthState
    data class Data(val isButtonEnabled: Boolean = false): AuthState
    data class Error(val errorText: String): AuthState
}