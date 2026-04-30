package com.personalizasv.app.ui.cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalizasv.app.data.models.Cliente
import com.personalizasv.app.data.repositories.ClienteRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClienteFormState(
    val nombreCompleto: String = "",
    val telefono: String = "",
    val correo: String = "",
    val direccion: String = "",
    val notas: String = ""
)

sealed class ClienteUiState {
    object Idle : ClienteUiState()
    object Loading : ClienteUiState()
    data class Success(val message: String) : ClienteUiState()
    data class Error(val message: String) : ClienteUiState()
}

class RegistrarClienteViewModel : ViewModel() {

    private val repository = ClienteRepositoryImpl()

    private val _formState = MutableStateFlow(ClienteFormState())
    val formState: StateFlow<ClienteFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<ClienteUiState>(ClienteUiState.Idle)
    val uiState: StateFlow<ClienteUiState> = _uiState.asStateFlow()

    fun onFieldChange(field: String, value: String) {
        _formState.value = when (field) {
            "nombre" -> _formState.value.copy(nombreCompleto = value)
            "telefono" -> _formState.value.copy(telefono = value)
            "correo" -> _formState.value.copy(correo = value)
            "direccion" -> _formState.value.copy(direccion = value)
            "notas" -> _formState.value.copy(notas = value)
            else -> _formState.value
        }
    }

    fun registrarCliente() {
        val form = _formState.value

        if (form.nombreCompleto.isBlank() || form.telefono.isBlank()) {
            _uiState.value = ClienteUiState.Error("Nombre y teléfono son obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = ClienteUiState.Loading

            val existente = repository.buscarPorTelefono(form.telefono)
            if (existente != null) {
                _uiState.value = ClienteUiState.Error("Ya existe un cliente con ese teléfono")
                return@launch
            }

            val cliente = Cliente(
                nombreCompleto = form.nombreCompleto.trim(),
                telefono = form.telefono.trim(),
                correo = form.correo.trim(),
                direccion = form.direccion.trim(),
                fechaRegistro = System.currentTimeMillis(),
                notas = form.notas.trim()
            )

            val result = repository.registrarCliente(cliente)
            result.fold(
                onSuccess = { id ->
                    _uiState.value = ClienteUiState.Success("Cliente registrado exitosamente")
                    _formState.value = ClienteFormState()
                },
                onFailure = { e ->
                    _uiState.value = ClienteUiState.Error("Error al registrar: ${e.message}")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = ClienteUiState.Idle
    }
}
