package com.personalizasv.app.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalizasv.app.data.models.Producto
import com.personalizasv.app.data.repositories.ProductoRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GestionProductosUiState {
    object Idle : GestionProductosUiState()
    object Loading : GestionProductosUiState()
    data class Success(val message: String) : GestionProductosUiState()
    data class Error(val message: String) : GestionProductosUiState()
}

class GestionProductosViewModel : ViewModel() {

    private val repository = ProductoRepositoryImpl()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _uiState = MutableStateFlow<GestionProductosUiState>(GestionProductosUiState.Idle)
    val uiState: StateFlow<GestionProductosUiState> = _uiState.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = GestionProductosUiState.Loading
            val lista = repository.obtenerTodosProductos()
            _productos.value = lista
            _uiState.value = GestionProductosUiState.Idle
        }
    }

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun productosFiltrados(): List<Producto> {
        val query = _busqueda.value.lowercase().trim()
        return if (query.isEmpty()) {
            _productos.value
        } else {
            _productos.value.filter {
                it.nombre.lowercase().contains(query) ||
                it.categoria.lowercase().contains(query)
            }
        }
    }

    fun desactivarProducto(id: String) {
        viewModelScope.launch {
            _uiState.value = GestionProductosUiState.Loading
            val result = repository.cambiarEstadoProducto(id, false)
            result.fold(
                onSuccess = {
                    _productos.value = _productos.value.map { p ->
                        if (p.id == id) p.copy(activo = false) else p
                    }
                    _uiState.value = GestionProductosUiState.Success("Producto desactivado")
                },
                onFailure = { e ->
                    _uiState.value = GestionProductosUiState.Error("Error: ${e.message}")
                }
            )
        }
    }

    fun activarProducto(id: String) {
        viewModelScope.launch {
            _uiState.value = GestionProductosUiState.Loading
            val result = repository.cambiarEstadoProducto(id, true)
            result.fold(
                onSuccess = {
                    _productos.value = _productos.value.map { p ->
                        if (p.id == id) p.copy(activo = true) else p
                    }
                    _uiState.value = GestionProductosUiState.Success("Producto activado")
                },
                onFailure = { e ->
                    _uiState.value = GestionProductosUiState.Error("Error: ${e.message}")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = GestionProductosUiState.Idle
    }
}
