package com.example.proyectoappmovil.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.repository.AuthRepository
import com.example.proyectoappmovil.data.repository.AuthResult
import com.example.proyectoappmovil.data.repository.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepo: AuthRepository,
    private val sessionManager: SessionManager
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

        viewModelScope.launch {
            val result = authRepo.login(_uiState.value.username.trim(), _uiState.value.password)

            when (result) {
                is AuthResult.Success -> {
                    sessionManager.saveUser(result.usuario)
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(result.usuario.nombre)
                }
                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.reason) }
                }
            }
        }
    }

    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val app = App.instance
                return LoginViewModel(
                    app.container.authRepository,
                    app.container.sessionManager
                ) as T
            }
        }
    }
}