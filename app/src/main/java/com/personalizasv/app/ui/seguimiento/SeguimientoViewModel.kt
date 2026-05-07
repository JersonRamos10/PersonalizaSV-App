package com.personalizasv.app.ui.seguimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalizasv.app.data.models.Pedido
import com.personalizasv.app.data.repositories.PedidoRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SeguimientoUiState {
    object Idle : SeguimientoUiState()
    object Loading : SeguimientoUiState()
    data class Success(val message: String) : SeguimientoUiState()
    data class Error(val message: String) : SeguimientoUiState()
}

class SeguimientoViewModel : ViewModel() {

    private val repository = PedidoRepositoryImpl()

    private val _todosPedidos = MutableStateFlow<List<Pedido>>(emptyList())
    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _pedidoSeleccionado = MutableStateFlow<Pedido?>(null)
    val pedidoSeleccionado: StateFlow<Pedido?> = _pedidoSeleccionado.asStateFlow()

    private val _uiState = MutableStateFlow<SeguimientoUiState>(SeguimientoUiState.Idle)
    val uiState: StateFlow<SeguimientoUiState> = _uiState.asStateFlow()

    init {
        cargarPedidos()
    }

    fun cargarPedidos() {
        viewModelScope.launch {
            _uiState.value = SeguimientoUiState.Loading
            val lista = repository.obtenerTodosPedidos()
            _todosPedidos.value = lista
            _pedidos.value = lista
            _uiState.value = SeguimientoUiState.Idle
        }
    }

    fun filtrarPedidos(query: String) {
        val q = query.lowercase().trim()
        _pedidos.value = if (q.isEmpty()) {
            _todosPedidos.value
        } else {
            _todosPedidos.value.filter { p ->
                p.nombreCliente.lowercase().contains(q) ||
                p.id.lowercase().contains(q) ||
                p.id.take(8).lowercase().contains(q)
            }
        }
    }

    fun seleccionarPedido(pedido: Pedido) {
        _pedidoSeleccionado.value = pedido
    }

    fun resetState() {
        _uiState.value = SeguimientoUiState.Idle
    }
}
