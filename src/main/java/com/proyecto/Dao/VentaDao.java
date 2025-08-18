package com.proyecto.Dao;

import com.proyecto.Domain.Carrito;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.sql.Types;
import java.util.Collections;
import org.springframework.jdbc.core.SqlOutParameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.format.DateTimeFormatter;

@Repository
public class VentaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper para las ventas
    private RowMapper<Carrito> ventaRowMapper = (rs, rowNum) -> {
        Carrito venta = new Carrito();
        venta.setVentaId(rs.getLong("VENTA_ID"));
        venta.setCodigoVenta(rs.getString("CODIGO_VENTA"));
        venta.setClienteId(rs.getLong("CLIENTE_ID"));
        venta.setCliente(rs.getString("CLIENTE")); 

        java.sql.Timestamp timestamp = rs.getTimestamp("FECHA_VENTA");
        if (timestamp != null) {
            venta.setFechaVenta(timestamp.toLocalDateTime());
        }

        venta.setSubtotal(rs.getDouble("SUBTOTAL"));
        venta.setImpuesto(rs.getDouble("IMPUESTO"));
        venta.setDescuento(rs.getDouble("DESCUENTO"));
        venta.setTotal(rs.getDouble("TOTAL"));
        venta.setMedioPago(rs.getString("MEDIO_PAGO"));
        venta.setEstado(rs.getString("ESTADO"));
        return venta;
    };

    // RowMapper para los detalles de venta
    private RowMapper<Carrito> detalleVentaRowMapper = (rs, rowNum) -> {
        Carrito detalle = new Carrito();
        detalle.setDetalleId(rs.getLong("DETALLE_ID"));
        detalle.setVentaId(rs.getLong("VENTA_ID"));
        detalle.setInventarioId(rs.getLong("INVENTARIO_ID"));
        detalle.setCantidad(rs.getInt("CANTIDAD"));
        detalle.setPrecioUnitario(rs.getDouble("PRECIO_UNITARIO"));
        detalle.setSubtotalDetalle(rs.getDouble("SUBTOTAL"));
        detalle.setDescuentoItem(rs.getDouble("DESCUENTO_ITEM"));
        return detalle;
    };

    // MÉTODO: Registrar venta completa usando procedimiento almacenado
    public Long registrarVentaCompleta(Carrito venta) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("registrar_venta_completa")
                .declareParameters(
                        new SqlParameter("p_codigo_venta", Types.VARCHAR),
                        new SqlParameter("p_cliente_id", Types.NUMERIC),
                        new SqlParameter("p_subtotal", Types.NUMERIC),
                        new SqlParameter("p_impuesto", Types.NUMERIC),
                        new SqlParameter("p_descuento", Types.NUMERIC),
                        new SqlParameter("p_total", Types.NUMERIC),
                        new SqlParameter("p_medio_pago", Types.VARCHAR),
                        new SqlParameter("p_estado", Types.VARCHAR),
                        new SqlOutParameter("p_venta_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_codigo_venta", venta.getCodigoVenta());
        inParams.put("p_cliente_id", venta.getClienteId());
        inParams.put("p_subtotal", venta.getSubtotal());
        inParams.put("p_impuesto", venta.getImpuesto());
        inParams.put("p_descuento", venta.getDescuento() != 0 ? venta.getDescuento() : 0.0);
        inParams.put("p_total", venta.getTotal());
        inParams.put("p_medio_pago", venta.getMedioPago());
        inParams.put("p_estado", venta.getEstado() != null ? venta.getEstado() : "Pendiente");

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            Number ventaId = (Number) result.get("p_venta_id");
            return ventaId != null ? ventaId.longValue() : null;
        } catch (Exception e) {
            System.err.println("Error detallado en registrarVentaCompleta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al registrar venta completa: " + e.getMessage(), e);
        }
    }

    // Registrar nueva venta usando procedimiento almacenado original
    public Long registrarVenta(Carrito venta) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("registrar_venta")
                .declareParameters(
                        new SqlParameter("p_cliente_id", Types.NUMERIC),
                        new SqlParameter("p_monto_total", Types.NUMERIC),
                        new SqlParameter("p_estado", Types.VARCHAR),
                        new SqlOutParameter("p_result", Types.NUMERIC)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_cliente_id", venta.getClienteId());
        inParams.put("p_monto_total", venta.getTotal());
        inParams.put("p_estado", venta.getEstado() != null ? venta.getEstado() : "Pendiente");

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            Number ventaId = (Number) result.get("p_result");
            return ventaId != null ? ventaId.longValue() : null;
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar venta: " + e.getMessage(), e);
        }
    }

    // Agregar detalle de venta usando procedimiento almacenado
    public void agregarDetalleVenta(Carrito detalle) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("agregar_detalle_venta")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.NUMERIC),
                        new SqlParameter("p_inventario_id", Types.NUMERIC),
                        new SqlParameter("p_cantidad", Types.NUMERIC),
                        new SqlParameter("p_precio_unitario", Types.NUMERIC),
                        new SqlParameter("p_subtotal", Types.NUMERIC),
                        new SqlParameter("p_descuento_item", Types.NUMERIC)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_venta_id", detalle.getVentaId());
        inParams.put("p_inventario_id", detalle.getInventarioId());
        inParams.put("p_cantidad", detalle.getCantidad());
        inParams.put("p_precio_unitario", detalle.getPrecioUnitario());
        inParams.put("p_subtotal", detalle.getSubtotalDetalle());
        inParams.put("p_descuento_item", detalle.getDescuentoItem() != 0 ? detalle.getDescuentoItem() : 0.0);

        try {
            jdbcCall.execute(inParams);
        } catch (Exception e) {
            System.err.println("Error al agregar detalle: " + e.getMessage());
            throw new RuntimeException("Error al agregar detalle de venta: " + e.getMessage(), e);
        }
    }

    // Cambiar estado de venta
    public void cambiarEstadoVenta(Long ventaId, String nuevoEstado) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("cambiar_estado_venta")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.NUMERIC),
                        new SqlParameter("p_nuevo_estado", Types.VARCHAR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_venta_id", ventaId);
        inParams.put("p_nuevo_estado", nuevoEstado);

        try {
            jdbcCall.execute(inParams);
        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar estado de venta: " + e.getMessage(), e);
        }
    }

    public List<Carrito> findVentasByClienteId(Long clienteId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_ventas_por_cliente")
                .declareParameters(
                        new SqlParameter("p_cliente_id", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_cliente_id", clienteId);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            List<Carrito> ventas = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito venta = mapRowToCarrito(row);
                    ventas.add(venta);
                }
            }
            return ventas;
        } catch (Exception e) {
            System.err.println("Error en findVentasByClienteId: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Carrito> findDetallesByVentaId(Long ventaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_detalles_por_venta")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_venta_id", ventaId);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            List<Carrito> detalles = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito detalle = mapRowToCarritoDetalle(row);
                    detalles.add(detalle);
                }
            }
            return detalles;
        } catch (Exception e) {
            System.err.println("Error en findDetallesByVentaId: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Carrito> findVentasByEstado(String estado) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_ventas_por_estado")
                .declareParameters(
                        new SqlParameter("p_estado", Types.VARCHAR),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_estado", estado);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            List<Carrito> ventas = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito venta = mapRowToCarrito(row);
                    ventas.add(venta);
                }
            }
            return ventas;
        } catch (Exception e) {
            System.err.println("Error en findVentasByEstado: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Carrito> findVentasDelDia() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_ventas_del_dia")
                .declareParameters(
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        try {
            Map<String, Object> result = jdbcCall.execute();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            List<Carrito> ventas = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito venta = mapRowToCarrito(row);
                    ventas.add(venta);
                }
            }
            return ventas;
        } catch (Exception e) {
            System.err.println("Error en findVentasDelDia: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void actualizarTotalesVenta(Long ventaId, double subtotal, double impuesto, double total) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("actualizar_totales_venta")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.NUMERIC),
                        new SqlParameter("p_subtotal", Types.NUMERIC),
                        new SqlParameter("p_impuesto", Types.NUMERIC),
                        new SqlParameter("p_total", Types.NUMERIC)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_venta_id", ventaId);
        inParams.put("p_subtotal", subtotal);
        inParams.put("p_impuesto", impuesto);
        inParams.put("p_total", total);

        try {
            jdbcCall.execute(inParams);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar totales de venta: " + e.getMessage(), e);
        }
    }

    public void eliminarDetalleVenta(Long detalleId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("eliminar_detalle_venta")
                .declareParameters(
                        new SqlParameter("p_detalle_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_detalle_id", detalleId);

        try {
            jdbcCall.execute(inParams);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar detalle de venta: " + e.getMessage(), e);
        }
    }

    public int contarVentas() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("fn_contar_ventas_totales")
                .returningResultSet("result", (rs, rowNum) -> rs.getInt(1));

        try {
            Integer result = jdbcCall.executeFunction(Integer.class);
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("Error al contar ventas: " + e.getMessage());
            return 0;
        }
    }

    public double obtenerTotalVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("fn_total_ventas_periodo")
                .declareParameters(
                        new SqlParameter("p_fecha_inicio", Types.TIMESTAMP),
                        new SqlParameter("p_fecha_fin", Types.TIMESTAMP)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_fecha_inicio", java.sql.Timestamp.valueOf(fechaInicio));
        inParams.put("p_fecha_fin", java.sql.Timestamp.valueOf(fechaFin));

        try {
            Double result = jdbcCall.executeFunction(Double.class, inParams);
            return result != null ? result : 0.0;
        } catch (Exception e) {
            System.err.println("Error al obtener total de ventas por período: " + e.getMessage());
            return 0.0;
        }
    }

    public List<Carrito> findDetallesVentaConProductos(Long ventaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_detalles_venta_con_productos")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_venta_id", ventaId);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            List<Carrito> detalles = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito detalle = mapRowToCarritoDetalleConProducto(row);
                    detalles.add(detalle);
                }
            }
            return detalles;
        } catch (Exception e) {
            System.err.println("Error al obtener detalles con productos: " + e.getMessage());
            return findDetallesByVentaId(ventaId); // Fallback
        }
    }

    public boolean existeVenta(Long ventaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("fn_existe_venta")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_venta_id", ventaId);

        try {
            Integer result = jdbcCall.executeFunction(Integer.class, inParams);
            return result != null && result == 1;
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de venta: " + e.getMessage());
            return false;
        }
    }

    public void cambiarEstadoVentaSeguro(Long ventaId, String nuevoEstado) throws Exception {
        // Primero validar que la venta existe
        if (!existeVenta(ventaId)) {
            throw new RuntimeException("Venta no encontrada con ID: " + ventaId);
        }

        // Validar estados válidos
        List<String> estadosValidos = Arrays.asList("Pendiente", "Confirmada", "En Proceso", "Completada", "Cancelada");
        if (!estadosValidos.contains(nuevoEstado)) {
            throw new RuntimeException("Estado no válido: " + nuevoEstado);
        }

        try {
            cambiarEstadoVenta(ventaId, nuevoEstado);
            System.out.println("Estado actualizado exitosamente. Venta ID: " + ventaId + ", Nuevo Estado: " + nuevoEstado);
        } catch (Exception e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado de venta: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> obtenerEstadisticasVentas() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("obtener_estadisticas_ventas")
                .declareParameters(
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Integer> estadisticas = new HashMap<>();
        estadisticas.put("Pendiente", 0);
        estadisticas.put("Confirmada", 0);
        estadisticas.put("Completada", 0);
        estadisticas.put("Cancelada", 0);
        estadisticas.put("En Proceso", 0);

        try {
            Map<String, Object> result = jdbcCall.execute();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    String estado = (String) row.get("ESTADO");
                    Number cantidad = (Number) row.get("CANTIDAD");
                    if (estado != null && cantidad != null) {
                        estadisticas.put(estado, cantidad.intValue());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }

        return estadisticas;
    }

    public List<Carrito> findAllVentas() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_ventas_totales")
                .declareParameters(
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        try {
            Map<String, Object> result = jdbcCall.execute();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            List<Carrito> ventas = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito venta = mapRowToCarrito(row);
                    ventas.add(venta);
                }
            }
            return ventas;
        } catch (Exception e) {
            System.err.println("Error en findAllVentas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Carrito findVentaById(Long ventaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("buscar_venta_por_id")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_venta_id", ventaId);

        try {
            System.out.println("Buscando venta con ID: " + ventaId);
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            if (rows != null && !rows.isEmpty()) {
                Carrito venta = mapRowToCarrito(rows.get(0));
                System.out.println("Venta encontrada: ID=" + venta.getVentaId() + ", Estado=" + venta.getEstado());
                return venta;
            } else {
                System.out.println("No se encontró venta con ID: " + ventaId);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al buscar venta por ID " + ventaId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Carrito> findVentasConBusqueda(String busqueda, String estado) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("buscar_ventas_filtros")
                .declareParameters(
                        new SqlParameter("p_busqueda", Types.VARCHAR),
                        new SqlParameter("p_estado", Types.VARCHAR),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_busqueda", busqueda);
        inParams.put("p_estado", estado);

        try {
            System.out.println("Ejecutando búsqueda con parámetros: busqueda=" + busqueda + ", estado=" + estado);
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            List<Carrito> resultados = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito venta = mapRowToCarrito(row);
                    resultados.add(venta);
                }
            }
            System.out.println("Búsqueda completada. Resultados encontrados: " + resultados.size());
            return resultados;
        } catch (Exception e) {
            System.err.println("Error en búsqueda de ventas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Carrito> findHistorialComprasPorCliente(Long clienteId) {
        return findHistorialComprasPorClienteSQL(clienteId);
    }

    public List<Carrito> findHistorialComprasPorClienteSQL(Long clienteId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_historial_compras_por_cliente")
                .declareParameters(
                        new SqlParameter("p_cliente_id", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_cliente_id", clienteId);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");

            List<Carrito> historial = new ArrayList<>();
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Carrito compra = new Carrito();
                    compra.setCodigoVenta((String) row.get("CODIGO_VENTA"));

                    java.sql.Timestamp timestamp = (java.sql.Timestamp) row.get("FECHA_VENTA");
                    if (timestamp != null) {
                        compra.setFechaVenta(timestamp.toLocalDateTime());
                    }

                    Object totalObj = row.get("TOTAL");
                    if (totalObj instanceof Number) {
                        compra.setTotal(((Number) totalObj).doubleValue());
                    }

                    compra.setEstado((String) row.get("ESTADO"));
                    compra.setClienteId(clienteId);
                    historial.add(compra);
                }
            }
            return historial;
        } catch (Exception e) {
            System.err.println("Error al obtener historial de compras: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Long findClienteIdPorUsername(String username) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("fn_buscar_cliente_por_username")
                .declareParameters(
                        new SqlParameter("p_username", Types.VARCHAR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_username", username);

        try {
            Long result = jdbcCall.executeFunction(Long.class, inParams);
            return result;
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por username: " + e.getMessage());
            return null;
        }
    }

    public boolean clienteEstaActivo(Long clienteId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("fn_cliente_esta_activo")
                .declareParameters(
                        new SqlParameter("p_cliente_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_cliente_id", clienteId);

        try {
            Integer result = jdbcCall.executeFunction(Integer.class, inParams);
            return result != null && result == 1;
        } catch (Exception e) {
            System.err.println("Error al verificar estado del cliente: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> obtenerInfoBasicaCliente(Long clienteId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("obtener_info_basica_cliente")
                .declareParameters(
                        new SqlParameter("p_cliente_id", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_cliente_id", clienteId);

        Map<String, Object> info = new HashMap<>();
        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
            
            if (rows != null && !rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                info.put("id", ((Number) row.get("ID_CLIENTE")).longValue());
                info.put("username", row.get("USERNAME"));
                info.put("nombre", row.get("NOMBRE"));
                info.put("apellidos", row.get("APELLIDOS"));
                info.put("email", row.get("EMAIL"));
                info.put("activo", ((Number) row.get("ACTIVO")).intValue() == 1);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener info del cliente: " + e.getMessage());
        }
        return info;
    }

    // Métodos auxiliares para mapear resultados
    private Carrito mapRowToCarrito(Map<String, Object> row) {
        Carrito venta = new Carrito();
        
        if (row.get("VENTA_ID") != null) {
            venta.setVentaId(((Number) row.get("VENTA_ID")).longValue());
        }
        venta.setCodigoVenta((String) row.get("CODIGO_VENTA"));
        if (row.get("CLIENTE_ID") != null) {
            venta.setClienteId(((Number) row.get("CLIENTE_ID")).longValue());
        }
        
        java.sql.Timestamp timestamp = (java.sql.Timestamp) row.get("FECHA_VENTA");
        if (timestamp != null) {
            venta.setFechaVenta(timestamp.toLocalDateTime());
        }
        
        if (row.get("SUBTOTAL") != null) {
            venta.setSubtotal(((Number) row.get("SUBTOTAL")).doubleValue());
        }
        if (row.get("IMPUESTO") != null) {
            venta.setImpuesto(((Number) row.get("IMPUESTO")).doubleValue());
        }
        if (row.get("DESCUENTO") != null) {
            venta.setDescuento(((Number) row.get("DESCUENTO")).doubleValue());
        }
        if (row.get("TOTAL") != null) {
            venta.setTotal(((Number) row.get("TOTAL")).doubleValue());
        }
        
        venta.setMedioPago((String) row.get("MEDIO_PAGO"));
        venta.setEstado((String) row.get("ESTADO"));
        
        // Manejar campo cliente si existe
        if (row.get("CLIENTE") != null) {
            venta.setCliente((String) row.get("CLIENTE"));
        } else if (venta.getClienteId() != null) {
            venta.setCliente("Cliente ID: " + venta.getClienteId());
        }
        
        return venta;
    }

    private Carrito mapRowToCarritoDetalle(Map<String, Object> row) {
        Carrito detalle = new Carrito();
        
        if (row.get("DETALLE_ID") != null) {
            detalle.setDetalleId(((Number) row.get("DETALLE_ID")).longValue());
        }
        if (row.get("VENTA_ID") != null) {
            detalle.setVentaId(((Number) row.get("VENTA_ID")).longValue());
        }
        if (row.get("INVENTARIO_ID") != null) {
            detalle.setInventarioId(((Number) row.get("INVENTARIO_ID")).longValue());
        }
        if (row.get("CANTIDAD") != null) {
            detalle.setCantidad(((Number) row.get("CANTIDAD")).intValue());
        }
        if (row.get("PRECIO_UNITARIO") != null) {
            detalle.setPrecioUnitario(((Number) row.get("PRECIO_UNITARIO")).doubleValue());
        }
        if (row.get("SUBTOTAL") != null) {
            detalle.setSubtotalDetalle(((Number) row.get("SUBTOTAL")).doubleValue());
        }
        if (row.get("DESCUENTO_ITEM") != null) {
            detalle.setDescuentoItem(((Number) row.get("DESCUENTO_ITEM")).doubleValue());
        }
        
        return detalle;
    }

    private Carrito mapRowToCarritoDetalleConProducto(Map<String, Object> row) {
        Carrito detalle = mapRowToCarritoDetalle(row);
        if (row.get("NOMBRE_PRODUCTO") != null) {
            detalle.setNombreProducto((String) row.get("NOMBRE_PRODUCTO"));
        }
        return detalle;
    }
}