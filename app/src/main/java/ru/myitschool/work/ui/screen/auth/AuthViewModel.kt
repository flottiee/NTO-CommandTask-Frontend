package ru.myitschool.work.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.domain.auth.CheckAndSaveAuthCodeUseCase

class AuthViewModel : ViewModel() {
    private val checkAndSaveAuthCodeUseCase by lazy { CheckAndSaveAuthCodeUseCase(AuthRepository) }
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Data())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()
    private val regex = Regex("^[a-zA-Z0-9]{4}$")
    private val _actionFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val actionFlow: SharedFlow<Unit> = _actionFlow

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Send -> {
                viewModelScope.launch(Dispatchers.Default) {
                    _uiState.update { AuthState.Loading }
                    checkAndSaveAuthCodeUseCase.invoke(intent.text).fold(
                        onSuccess = {
                            _actionFlow.emit(Unit)
                        },
                        onFailure = { error ->
                            error.printStackTrace()
                            _uiState.update { AuthState.Error(error.message?.ifEmpty { "error" } ?: "null error") }
                        }
                    )
                }
            }
            is AuthIntent.TextInput -> {
                val isButtonEnabled = isButtonEnabled(intent.text)
                Log.d("TAG", "viewmodel  process: ${intent}, $isButtonEnabled")
                _uiState.update { AuthState.Data(
                        isButtonEnabled = isButtonEnabled
                    ) }
            }
        }
    }

    private fun isButtonEnabled(inputText: String): Boolean {
        return (regex.matches(inputText))
    }
}