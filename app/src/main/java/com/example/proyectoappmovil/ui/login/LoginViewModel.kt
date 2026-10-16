package com.example.proyectoappmovil.ui.login

import androidx.lifecycle.ViewModel
import com.example.proyectoappmovil.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun submit(onSuccess: (String) -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val ok = repo.login(_uiState.value.username.trim(), _uiState.value.password)

        if (ok) {
            _uiState.update { it.copy(isLoading = false) }
            onSuccess(_uiState.value.username.trim())
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Credenciales inválidas") }
        }
    }
}
