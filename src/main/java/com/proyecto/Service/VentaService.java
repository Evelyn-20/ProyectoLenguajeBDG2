package com.proyecto.Service;

import com.proyecto.Dao.*;
import com.proyecto.Domain.*;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service
public class VentaService {

    @Autowired
    private VentaDao ventaDao;
    @Autowired
    private ClienteDao clienteDao;

    // Procesar una venta completa (carrito de compras)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long procesarVenta(Carrito ventaHeader, List<Carrito> detallesVenta) {
        try {
            // Validar datos de la venta
            validarDatosVenta(ventaHeader, detallesVenta);

            // Calcular totales si no están calculados
            calcularTotales(ventaHeader, detallesVenta);

            // Generar código de venta si no existe
            if (ventaHeader.getCodigoVenta() == null || ventaHeader.getCodigoVenta().isEmpty()) {
                ventaHeader.setCodigoVenta("V" + System.currentTimeMillis());
            }

            // Log para debugging
            System.out.println("Procesando venta:");
            System.out.println("Cliente ID: " + ventaHeader.getClienteId());
            System.out.println("Código: " + ventaHeader.getCodigoVenta());
            System.out.println("Total: " + ventaHeader.getTotal());
            System.out.println("Medio pago: " + ventaHeader.getMedioPago());

            // Registrar la venta principal usando el método SQL directo (más confiable)
            Long ventaId = ventaDao.registrarVentaCompleta(ventaHeader);

            if (ventaId == null) {
                throw new RuntimeException("Error al obtener ID de venta generado");
            }

            System.out.println("Venta registrada con ID: " + ventaId);

            // Registrar cada detalle de la venta
            for (int i = 0; i < detallesVenta.size(); i++) {
                Carrito detalle = detallesVenta.get(i);
                detalle.setVentaId(ventaId);

                System.out.println("Agregando detalle " + (i + 1) + ":");
                System.out.println("  Inventario ID: " + detalle.getInventarioId());
                System.out.println("  Cantidad: " + detalle.getCantidad());
                System.out.println("  Precio: " + detalle.getPrecioUnitario());
                System.out.println("  Subtotal: " + detalle.getSubtotalDetalle());

                ventaDao.agregarDetalleVenta(detalle);
            }

            System.out.println("Venta procesada exitosamente. ID: " + ventaId);
            return ventaId;

        } catch (Exception e) {
            System.err.println("Error detallado al procesar venta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al procesar venta: " + e.getMessage(), e);
        }
    }

    // Registrar venta simple
    @Transactional(rollbackFor = Exception.class)
    public Long registrarVenta(Carrito venta) {
        try {
            validarVentaBasica(venta);
            return ventaDao.registrarVenta(venta);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar venta: " + e.getMessage(), e);
        }
    }

    // Agregar producto al carrito (detalle de venta)
    @Transactional(rollbackFor = Exception.class)
    public void agregarProductoAlCarrito(Long ventaId, Long inventarioId, int cantidad, double precioUnitario) {
        try {
            Carrito detalle = new Carrito();
            detalle.setVentaId(ventaId);
            detalle.setInventarioId(inventarioId);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotalDetalle(cantidad * precioUnitario);
            detalle.setDescuentoItem(0.0);

            ventaDao.agregarDetalleVenta(detalle);

            // Recalcular totales de la venta
            recalcularTotalesVenta(ventaId);

        } catch (Exception e) {
            throw new RuntimeException("Error al agregar producto al carrito: " + e.getMessage(), e);
        }
    }

    // Cambiar estado de venta
    @Transactional(rollbackFor = Exception.class)
    public void cambiarEstadoVenta(Long ventaId, String nuevoEstado) {
        try {
            // Validar que el estado sea válido
            if (!esEstadoValido(nuevoEstado)) {
                throw new RuntimeException("Estado no válido: " + nuevoEstado);
            }

            ventaDao.cambiarEstadoVenta(ventaId, nuevoEstado);

        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar estado de venta: " + e.getMessage(), e);
        }
    }

    // Confirmar pago de venta
    @Transactional(rollbackFor = Exception.class)
    public void confirmarPago(Long ventaId, String medioPago) {
        try {
            Carrito venta = ventaDao.findVentaById(ventaId);
            if (venta == null) {
                throw new RuntimeException("Venta no encontrada");
            }

            // Actualizar medio de pago si se proporciona
            if (medioPago != null && !medioPago.isEmpty()) {
                // Aquí puedes agregar lógica para actualizar el medio de pago
                // Por ahora solo cambiamos el estado
            }

            ventaDao.cambiarEstadoVenta(ventaId, "Confirmada");

        } catch (Exception e) {
            throw new RuntimeException("Error al confirmar pago: " + e.getMessage(), e);
        }
    }

    // Obtener todas las ventas
    @Transactional(readOnly = true)
    public List<Carrito> getVentas() {
        return ventaDao.findAllVentas();
    }

    // Obtener ventas por cliente
    @Transactional(readOnly = true)
    public List<Carrito> getVentasByCliente(Long clienteId) {
        return ventaDao.findVentasByClienteId(clienteId);
    }

    // Obtener venta por ID
    @Transactional(readOnly = true)
    public Carrito getVentaById(Long ventaId) {
        return ventaDao.findVentaById(ventaId);
    }

    // Obtener detalles de venta
    @Transactional(readOnly = true)
    public List<Carrito> getDetallesVenta(Long ventaId) {
        return ventaDao.findDetallesByVentaId(ventaId);
    }

    // Obtener venta completa con detalles
    @Transactional(readOnly = true)
    public List<Carrito> getVentaCompleta(Long ventaId) {
        return (List<Carrito>) ventaDao.findVentaById(ventaId);
    }

    // Obtener ventas por estado
    @Transactional(readOnly = true)
    public List<Carrito> getVentasByEstado(String estado) {
        return ventaDao.findVentasByEstado(estado);
    }

    // Obtener ventas pendientes
    @Transactional(readOnly = true)
    public List<Carrito> getVentasPendientes() {
        return ventaDao.findVentasByEstado("Pendiente");
    }

    // Obtener ventas confirmadas
    @Transactional(readOnly = true)
    public List<Carrito> getVentasConfirmadas() {
        return ventaDao.findVentasByEstado("Confirmada");
    }

    // Obtener ventas del día
    @Transactional(readOnly = true)
    public List<Carrito> getVentasDelDia() {
        return ventaDao.findVentasDelDia();
    }

    // Cancelar venta
    @Transactional(rollbackFor = Exception.class)
    public void cancelarVenta(Long ventaId) {
        cambiarEstadoVenta(ventaId, "Cancelada");
    }

    // Completar venta
    @Transactional(rollbackFor = Exception.class)
    public void completarVenta(Long ventaId) {
        cambiarEstadoVenta(ventaId, "Completada");
    }

    // Eliminar detalle de venta
    @Transactional(rollbackFor = Exception.class)
    public void eliminarDetalleVenta(Long detalleId) {
        try {
            ventaDao.eliminarDetalleVenta(detalleId);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar detalle de venta: " + e.getMessage(), e);
        }
    }

    // Obtener estadísticas de ventas
    @Transactional(readOnly = true)
    public int getTotalVentas() {
        return ventaDao.contarVentas();
    }

    // Obtener total de ventas por período
    @Transactional(readOnly = true)
    public double getTotalVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaDao.obtenerTotalVentasPorPeriodo(fechaInicio, fechaFin);
    }

    // Validaciones privadas
    private void validarDatosVenta(Carrito ventaHeader, List<Carrito> detallesVenta) {
        if (ventaHeader == null) {
            throw new RuntimeException("Datos de venta no pueden ser nulos");
        }

        if (ventaHeader.getClienteId() == null || ventaHeader.getClienteId() <= 0) {
            throw new RuntimeException("Cliente ID es obligatorio y debe ser válido");
        }

        if (detallesVenta == null || detallesVenta.isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un detalle");
        }

        for (Carrito detalle : detallesVenta) {
            if (detalle.getInventarioId() == null || detalle.getInventarioId() <= 0) {
                throw new RuntimeException("Inventario ID es obligatorio en los detalles");
            }
            if (detalle.getCantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a cero");
            }
            if (detalle.getPrecioUnitario() <= 0) {
                throw new RuntimeException("El precio unitario debe ser mayor a cero");
            }
        }
    }

    private void validarVentaBasica(Carrito venta) {
        if (venta == null) {
            throw new RuntimeException("Datos de venta no pueden ser nulos");
        }

        if (venta.getClienteId() == null || venta.getClienteId() <= 0) {
            throw new RuntimeException("Cliente ID es obligatorio");
        }

        if (venta.getTotal() <= 0) {
            throw new RuntimeException("El total debe ser mayor a cero");
        }
    }

    private void calcularTotales(Carrito ventaHeader, List<Carrito> detallesVenta) {
        double subtotal = 0.0;

        // Calcular subtotal de todos los detalles
        for (Carrito detalle : detallesVenta) {
            double subtotalDetalle = detalle.getCantidad() * detalle.getPrecioUnitario();
            detalle.setSubtotalDetalle(subtotalDetalle);
            subtotal += subtotalDetalle;
        }

        // Aplicar descuentos si existen
        double descuentoTotal = ventaHeader.getDescuento() != 0 ? ventaHeader.getDescuento() : 0.0;
        double subtotalConDescuento = subtotal - descuentoTotal;

        // Calcular impuesto (13% en Costa Rica)
        double impuesto = subtotalConDescuento * 0.13;

        // Calcular total
        double total = subtotalConDescuento + impuesto;

        // Establecer valores calculados
        ventaHeader.setSubtotal(subtotal);
        ventaHeader.setImpuesto(impuesto);
        ventaHeader.setTotal(total);
    }

    private void recalcularTotalesVenta(Long ventaId) {
        try {
            // Obtener todos los detalles de la venta
            List<Carrito> detalles = ventaDao.findDetallesByVentaId(ventaId);

            double subtotal = 0.0;
            for (Carrito detalle : detalles) {
                subtotal += detalle.getSubtotalDetalle();
            }

            // Calcular impuesto (13%)
            double impuesto = subtotal * 0.13;
            double total = subtotal + impuesto;

            // Actualizar totales en la venta
            ventaDao.actualizarTotalesVenta(ventaId, subtotal, impuesto, total);

        } catch (Exception e) {
            throw new RuntimeException("Error al recalcular totales: " + e.getMessage(), e);
        }
    }

    private boolean esEstadoValido(String estado) {
        return estado != null && (estado.equals("Pendiente")
                || estado.equals("Confirmada")
                || estado.equals("Completada")
                || estado.equals("Cancelada")
                || estado.equals("En Proceso"));
    }

    @Transactional(readOnly = true)
    public List<Carrito> buscarVentas(String busqueda, String estado) {
        try {
            return ventaDao.findVentasConBusqueda(busqueda, estado);
        } catch (Exception e) {
            System.err.println("Error en búsqueda de ventas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Carrito> getDetallesVentaConProductos(Long ventaId) {
        try {
            return ventaDao.findDetallesVentaConProductos(ventaId);
        } catch (Exception e) {
            System.err.println("Error al obtener detalles con productos: " + e.getMessage());
            return ventaDao.findDetallesByVentaId(ventaId); // Fallback
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cambiarEstadoVentaSeguro(Long ventaId, String nuevoEstado) throws Exception {
        try {
            // Validar que el estado sea válido
            if (!esEstadoValido(nuevoEstado)) {
                throw new RuntimeException("Estado no válido: " + nuevoEstado);
            }

            // Validar que la venta existe
            if (!ventaDao.existeVenta(ventaId)) {
                throw new RuntimeException("Venta no encontrada con ID: " + ventaId);
            }

            // Obtener estado actual para validaciones de transición
            Carrito ventaActual = ventaDao.findVentaById(ventaId);
            if (ventaActual == null) {
                throw new RuntimeException("No se pudo cargar la venta");
            }

            // Validar transiciones de estado
            validarTransicionEstado(ventaActual.getEstado(), nuevoEstado);

            // Cambiar el estado
            ventaDao.cambiarEstadoVentaSeguro(ventaId, nuevoEstado);

            System.out.println("Estado cambiado exitosamente - Venta ID: " + ventaId
                    + ", Estado anterior: " + ventaActual.getEstado()
                    + ", Nuevo estado: " + nuevoEstado);

        } catch (Exception e) {
            System.err.println("Error en cambiarEstadoVentaSeguro: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado de venta: " + e.getMessage(), e);
        }
    }

    private void validarTransicionEstado(String estadoActual, String nuevoEstado) throws Exception {
        if (estadoActual == null) {
            return; // Permitir cualquier estado inicial
        }

        // Reglas de transición de estados
        switch (estadoActual) {
            case "Pendiente":
                if (!Arrays.asList("Confirmada", "Cancelada", "En Proceso").contains(nuevoEstado)) {
                    throw new RuntimeException("No se puede cambiar de 'Pendiente' a '" + nuevoEstado + "'");
                }
                break;
            case "Confirmada":
                if (!Arrays.asList("En Proceso", "Completada", "Cancelada").contains(nuevoEstado)) {
                    throw new RuntimeException("No se puede cambiar de 'Confirmada' a '" + nuevoEstado + "'");
                }
                break;
            case "En Proceso":
                if (!Arrays.asList("Completada", "Cancelada").contains(nuevoEstado)) {
                    throw new RuntimeException("No se puede cambiar de 'En Proceso' a '" + nuevoEstado + "'");
                }
                break;
            case "Completada":
                throw new RuntimeException("No se puede cambiar el estado de una venta completada");
            case "Cancelada":
                throw new RuntimeException("No se puede cambiar el estado de una venta cancelada");
            default:
                throw new RuntimeException("Estado actual no reconocido: " + estadoActual);
        }
    }

    public List<String> getEstadosValidosParaTransicion(String estadoActual) {
        if (estadoActual == null) {
            return Arrays.asList("Pendiente", "Confirmada", "En Proceso");
        }

        switch (estadoActual) {
            case "Pendiente":
                return Arrays.asList("Confirmada", "Cancelada", "En Proceso");
            case "Confirmada":
                return Arrays.asList("En Proceso", "Completada", "Cancelada");
            case "En Proceso":
                return Arrays.asList("Completada", "Cancelada");
            case "Completada":
            case "Cancelada":
                return Arrays.asList(); // No se pueden cambiar
            default:
                return Arrays.asList("Pendiente");
        }
    }

    // Método para validar si una venta puede ser modificada
    public boolean puedeModificarVenta(Long ventaId) {
        try {
            Carrito venta = ventaDao.findVentaById(ventaId);
            if (venta == null) {
                return false;
            }

            String estado = venta.getEstado();
            return !Arrays.asList("Completada", "Cancelada").contains(estado);
        } catch (Exception e) {
            return false;
        }
    }

    // Agregar estos métodos al final de la clase VentaService
    // Obtener historial de compras por cliente
    @Transactional(readOnly = true)
    public List<Carrito> getHistorialComprasPorCliente(Long clienteId) {
        try {
            if (clienteId == null || clienteId <= 0) {
                throw new RuntimeException("Cliente ID no válido");
            }

            // Usar el método del procedimiento almacenado, con fallback a SQL directo
            List<Carrito> historial = ventaDao.findHistorialComprasPorClienteSQL(clienteId);

            System.out.println("Historial de compras obtenido para cliente " + clienteId + ": " + historial.size() + " compras");

            return historial;

        } catch (Exception e) {
            System.err.println("Error al obtener historial de compras: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Verificar si una compra pertenece a un cliente específico
    @Transactional(readOnly = true)
    public boolean compraPerteneceACliente(Long ventaId, Long clienteId) {
        try {
            Carrito venta = ventaDao.findVentaById(ventaId);
            return venta != null && venta.getClienteId().equals(clienteId);
        } catch (Exception e) {
            System.err.println("Error al verificar propiedad de compra: " + e.getMessage());
            return false;
        }
    }

    // Validar si una compra puede ser cancelada por el cliente
    public boolean puedeClienteCancelarCompra(Long ventaId, Long clienteId) {
        try {
            Carrito venta = ventaDao.findVentaById(ventaId);

            if (venta == null) {
                return false;
            }

            // Verificar que la compra pertenece al cliente
            if (!venta.getClienteId().equals(clienteId)) {
                return false;
            }

            // Verificar que el estado permite cancelación
            String estado = venta.getEstado();
            return estado != null
                    && !estado.equals("Completada")
                    && !estado.equals("Cancelada");

        } catch (Exception e) {
            System.err.println("Error al validar cancelación de compra: " + e.getMessage());
            return false;
        }
    }

    public Long getClienteIdPorUsername(String username) {
        try {
            System.out.println("Buscando cliente con email/cédula: " + username);

            // Primero buscar por email
            Cliente cliente = clienteDao.obtenerClientePorEmail(username);

            // Si no se encuentra por email, buscar por cédula
            if (cliente == null) {
                cliente = clienteDao.obtenerClientePorCedula(username);
            }

            if (cliente != null) {
                System.out.println("Cliente encontrado - ID: " + cliente.getIdCliente() + " para email/cédula: " + username);
                return cliente.getIdCliente();
            }

            System.out.println("No se encontró cliente con email/cédula: " + username);
            return null;

        } catch (Exception e) {
            System.err.println("Error al buscar cliente por email/cédula: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
