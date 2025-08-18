package com.proyecto.Dao;

import com.proyecto.Domain.Asociacion;
import com.proyecto.Domain.Producto;
import com.proyecto.Domain.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class AsociacionDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper para asociaciones con información completa
    private static final RowMapper<Asociacion> asociacionRowMapper = new RowMapper<Asociacion>() {
        @Override
        public Asociacion mapRow(ResultSet rs, int rowNum) throws SQLException {
            Asociacion asociacion = new Asociacion();
            asociacion.setProductoProveedorId(rs.getLong("PRODUCTO_PROVEEDOR_ID"));
            asociacion.setProductoId(rs.getLong("PRODUCTO_ID"));
            asociacion.setProveedorId(rs.getLong("PROVEEDOR_ID"));

            // Manejo seguro del precio
            Double precio = rs.getDouble("PRECIO_COMPRA");
            if (!rs.wasNull()) {
                asociacion.setPrecioCompra(precio);
            }

            asociacion.setEstado(rs.getString("ESTADO"));

            // Información adicional para mostrar en la vista
            try {
                asociacion.setNombreProducto(rs.getString("NOMBRE_PRODUCTO"));

                // Código como Long para consistencia
                Long codigo = rs.getLong("CODIGO_PRODUCTO");
                if (!rs.wasNull()) {
                    asociacion.setCodigoProducto(codigo);
                }

                asociacion.setNombreProveedor(rs.getString("NOMBRE_PROVEEDOR"));
                asociacion.setApellidosProveedor(rs.getString("APELLIDOS_PROVEEDOR"));
            } catch (SQLException e) {
                // Campos opcionales, no hacer nada si no existen
            }

            return asociacion;
        }
    };

    // RowMapper para productos
    private static final RowMapper<Producto> productoRowMapper = new RowMapper<Producto>() {
        @Override
        public Producto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Producto producto = new Producto();
            producto.setIdProducto(rs.getLong("ID_PRODUCTO"));
            producto.setCodigo(rs.getLong("CODIGO_PRODUCTO"));
            producto.setNombre(rs.getString("NOMBRE_PRODUCTO"));
            return producto;
        }
    };

    // RowMapper para proveedores
    private static final RowMapper<Proveedor> proveedorRowMapper = new RowMapper<Proveedor>() {
        @Override
        public Proveedor mapRow(ResultSet rs, int rowNum) throws SQLException {
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(rs.getLong("ID_PROVEEDOR"));
            proveedor.setNombre(rs.getString("NOMBRE"));
            proveedor.setApellidos(rs.getString("APELLIDOS"));
            return proveedor;
        }
    };

    public void registrarAsociacion(Asociacion asociacion) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("REGISTRAR_ASOCIACION_PROVEEDOR_PRODUCTO");

            Map<String, Object> params = new HashMap<>();
            params.put("p_producto_id", asociacion.getProductoId());
            params.put("p_proveedor_id", asociacion.getProveedorId());
            params.put("p_precio_compra", asociacion.getPrecioCompra());

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar asociación: " + e.getMessage());
        }
    }

    public void actualizarAsociacion(Asociacion asociacion) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("ACTUALIZAR_ASOCIACION_PRODUCTO_PROVEEDOR");

            Map<String, Object> params = new HashMap<>();
            params.put("p_asociacion_id", asociacion.getProductoProveedorId());
            params.put("p_nuevo_producto_id", asociacion.getProductoId());
            params.put("p_nuevo_proveedor_id", asociacion.getProveedorId());
            params.put("p_nuevo_precio", asociacion.getPrecioCompra());

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar asociación: " + e.getMessage());
        }
    }

    public void deshabilitarAsociacion(Long asociacionId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("DESHABILITAR_ASOCIACION_PRODUCTO_PROVEEDOR");

            Map<String, Object> params = new HashMap<>();
            params.put("p_asociacion_id", asociacionId);

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al deshabilitar asociación: " + e.getMessage());
        }
    }

    public void habilitarAsociacion(Long asociacionId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("HABILITAR_ASOCIACION_PRODUCTO_PROVEEDOR");

            Map<String, Object> params = new HashMap<>();
            params.put("p_asociacion_id", asociacionId);

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al habilitar asociación: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asociacion> obtenerAsociacionesActivas() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("OBTENER_ASOCIACIONES_ACTIVAS")
                    .returningResultSet("p_result", asociacionRowMapper);

            Map<String, Object> result = jdbcCall.execute();
            return (List<Asociacion>) result.get("p_result");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener asociaciones activas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asociacion> obtenerAsociacionesInactivas() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("OBTENER_ASOCIACIONES_INACTIVAS")
                    .returningResultSet("p_result", asociacionRowMapper);

            Map<String, Object> result = jdbcCall.execute();
            return (List<Asociacion>) result.get("p_result");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener asociaciones inactivas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asociacion> obtenerTodasLasAsociaciones() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("OBTENER_TODAS_ASOCIACIONES")
                    .returningResultSet("p_result", asociacionRowMapper);

            Map<String, Object> result = jdbcCall.execute();
            return (List<Asociacion>) result.get("p_result");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todas las asociaciones: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asociacion> buscarAsociaciones(String busqueda) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("BUSCAR_ASOCIACIONES")
                    .returningResultSet("p_result", asociacionRowMapper);

            Map<String, Object> params = new HashMap<>();
            params.put("p_busqueda", busqueda);

            Map<String, Object> result = jdbcCall.execute(params);
            return (List<Asociacion>) result.get("p_result");
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar asociaciones: " + e.getMessage());
        }
    }

    public Asociacion obtenerAsociacionPorId(Long id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("OBTENER_ASOCIACION_POR_ID")
                    .returningResultSet("p_result", asociacionRowMapper);

            Map<String, Object> params = new HashMap<>();
            params.put("p_id", id);

            Map<String, Object> result = jdbcCall.execute(params);
            @SuppressWarnings("unchecked")
            List<Asociacion> asociaciones = (List<Asociacion>) result.get("p_result");
            return asociaciones.isEmpty() ? null : asociaciones.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener asociación por ID: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Producto> obtenerProductosActivos() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("OBTENER_PRODUCTOS_ACTIVOS")
                    .returningResultSet("p_result", productoRowMapper);

            Map<String, Object> result = jdbcCall.execute();
            return (List<Producto>) result.get("p_result");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos activos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Proveedor> obtenerProveedoresActivos() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("OBTENER_PROVEEDORES_ACTIVOS")
                    .returningResultSet("p_result", proveedorRowMapper);

            Map<String, Object> result = jdbcCall.execute();
            return (List<Proveedor>) result.get("p_result");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener proveedores activos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asociacion> consultarAsociacionPorCodigoProducto(String codigoProducto) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("CONSULTAR_ASOCIACION_PRODUCTO_PROVEEDOR")
                    .returningResultSet("p_result", asociacionRowMapper);

            Map<String, Object> params = new HashMap<>();
            params.put("p_codigo_producto", codigoProducto);

            Map<String, Object> result = jdbcCall.execute(params);
            return (List<Asociacion>) result.get("p_result");
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar asociación: " + e.getMessage());
        }
    }

    public boolean existeAsociacionActiva(Long productoId, Long proveedorId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("EXISTE_ASOCIACION_ACTIVA");

            Map<String, Object> params = new HashMap<>();
            params.put("p_producto_id", productoId);
            params.put("p_proveedor_id", proveedorId);

            Map<String, Object> result = jdbcCall.execute(params);
            Number count = (Number) result.get("p_existe");
            return count != null && count.intValue() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de asociación: " + e.getMessage());
        }
    }
}