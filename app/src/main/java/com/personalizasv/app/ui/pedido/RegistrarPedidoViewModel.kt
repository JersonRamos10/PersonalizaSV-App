package com.personalizasv.app.ui.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalizasv.app.data.models.Cliente
import com.personalizasv.app.data.models.ItemPedido
import com.personalizasv.app.data.models.Pedido
import com.personalizasv.app.data.models.Personalizacion
import com.personalizasv.app.data.models.Producto
import com.personalizasv.app.data.repositories.ClienteRepositoryImpl
import com.personalizasv.app.data.repositories.PedidoRepositoryImpl
import com.personalizasv.app.data.repositories.ProductoRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PedidoFormState(
    val cliente: Cliente? = null,
    val busquedaTelefono: String = "",
    val items: List<CartItemPedido> = emptyList(),
    val metodoPago: String = "",
    val fechaEntregaEstimada: Long = 0L,
    val direccionEntrega: String = "",
    val notasAdicionales: String = ""
)

data class CartItemPedido(
    val producto: Producto,
    val cantidad: Int = 1,
    val personalizacion: Personalizacion = Personalizacion()
) {
    val subtotal: Double
        get() = producto.precioBase * cantidad
}

sealed class PedidoUiState {
    object Idle : PedidoUiState()
    object Loading : PedidoUiState()
    data class Success(val message: String) : PedidoUiState()
    data class Error(val message: String) : PedidoUiState()
}

class RegistrarPedidoViewModel : ViewModel() {

    private val clienteRepository = ClienteRepositoryImpl()
    private val productoRepository = ProductoRepositoryImpl()
    private val pedidoRepository = PedidoRepositoryImpl()

    private val _formState = MutableStateFlow(PedidoFormState())
    val formState: StateFlow<PedidoFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<PedidoUiState>(PedidoUiState.Idle)
    val uiState: StateFlow<PedidoUiState> = _uiState.asStateFlow()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _clienteBusqueda = MutableStateFlow<Cliente?>(null)
    val clienteBusqueda: StateFlow<Cliente?> = _clienteBusqueda.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = productoRepository.obtenerProductosActivos()
        }
    }

    fun buscarClientePorTelefono(telefono: String) {
        _formState.value = _formState.value.copy(busquedaTelefono = telefono)
        if (telefono.length < 8) {
            _clienteBusqueda.value = null
            return
        }
        viewModelScope.launch {
            _clienteBusqueda.value = clienteRepository.buscarPorTelefono(telefono)
        }
    }

    fun seleccionarCliente(cliente: Cliente) {
        _formState.value = _formState.value.copy(
            cliente = cliente,
            direccionEntrega = cliente.direccion
        )
    }

    fun agregarProducto(producto: Producto) {
        val currentItems = _formState.value.items.toMutableList()
        val existente = currentItems.find { it.producto.id == producto.id }
        if (existente != null) {
            currentItems[currentItems.indexOf(existente)] = existente.copy(cantidad = existente.cantidad + 1)
        } else {
            currentItems.add(CartItemPedido(producto = producto))
        }
        _formState.value = _formState.value.copy(items = currentItems)
    }

    fun actualizarCantidad(index: Int, cantidad: Int) {
        if (cantidad <= 0) {
            eliminarItem(index)
            return
        }
        val currentItems = _formState.value.items.toMutableList()
        currentItems[index] = currentItems[index].copy(cantidad = cantidad)
        _formState.value = _formState.value.copy(items = currentItems)
    }

    fun actualizarPersonalizacion(index: Int, personalizacion: Personalizacion) {
        val currentItems = _formState.value.items.toMutableList()
        currentItems[index] = currentItems[index].copy(personalizacion = personalizacion)
        _formState.value = _formState.value.copy(items = currentItems)
    }

    fun eliminarItem(index: Int) {
        val currentItems = _formState.value.items.toMutableList()
        currentItems.removeAt(index)
        _formState.value = _formState.value.copy(items = currentItems)
    }

    fun onFieldChange(field: String, value: String) {
        _formState.value = when (field) {
            "metodoPago" -> _formState.value.copy(metodoPago = value)
            "direccion" -> _formState.value.copy(direccionEntrega = value)
            "notas" -> _formState.value.copy(notasAdicionales = value)
            else -> _formState.value
        }
    }

    fun setFechaEntrega(timestamp: Long) {
        _formState.value = _formState.value.copy(fechaEntregaEstimada = timestamp)
    }

    val totalPedido: Double
        get() = _formState.value.items.sumOf { it.subtotal }

    fun registrarPedido() {
        val form = _formState.value

        if (form.cliente == null) {
            _uiState.value = PedidoUiState.Error("Debe seleccionar un cliente")
            return
        }
        if (form.items.isEmpty()) {
            _uiState.value = PedidoUiState.Error("Debe agregar al menos un producto")
            return
        }
        if (form.metodoPago.isBlank()) {
            _uiState.value = PedidoUiState.Error("Debe seleccionar un método de pago")
            return
        }
        if (form.fechaEntregaEstimada == 0L) {
            _uiState.value = PedidoUiState.Error("Debe seleccionar fecha de entrega")
            return
        }

        viewModelScope.launch {
            _uiState.value = PedidoUiState.Loading

            val items = form.items.map {
                ItemPedido(
                    cantidad = it.cantidad,
                    idProducto = it.producto.id,
                    nombreProducto = it.producto.nombre,
                    precioUnitario = it.producto.precioBase,
                    personalizacion = it.personalizacion
                )
            }

            val pedido = Pedido(
                idCliente = form.cliente.id,
                estado = "pendiente",
                productos = items,
                metodoPago = form.metodoPago,
                totalPedido = totalPedido,
                fechaPedido = System.currentTimeMillis(),
                fechaEntregaEstimada = form.fechaEntregaEstimada,
                direccionEntrega = form.direccionEntrega.trim(),
                notasAdicionales = form.notasAdicionales.trim()
            )

            val result = pedidoRepository.crearPedido(pedido)
            result.fold(
                onSuccess = { id ->
                    _uiState.value = PedidoUiState.Success("Pedido registrado exitosamente")
                    _formState.value = PedidoFormState()
                    _clienteBusqueda.value = null
                },
                onFailure = { e ->
                    _uiState.value = PedidoUiState.Error("Error al registrar: ${e.message}")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = PedidoUiState.Idle
    }
}
