package com.proyecto.Dao;

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
public class ProveedorDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper para convertir ResultSet a Proveedor
    private static final RowMapper<Proveedor> proveedorRowMapper = new RowMapper<Proveedor>() {
        @Override
        public Proveedor mapRow(ResultSet rs, int rowNum) throws SQLException {
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(rs.getLong("ID_PROVEEDOR"));
            proveedor.setCedula(rs.getString("CEDULA"));
            proveedor.setNombre(rs.getString("NOMBRE"));
            proveedor.setApellidos(rs.getString("APELLIDOS"));
            proveedor.setEmail(rs.getString("EMAIL"));
            proveedor.setTelefono(rs.getString("TELEFONO"));
            proveedor.setDireccion(rs.getString("DIRECCION"));
            proveedor.setEstado(rs.getString("ESTADO"));
            return proveedor;
        }
    };

    // Método para registrar proveedor usando procedimiento almacenado
    public void registrarProveedor(Proveedor proveedor) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("REGISTRAR_PROVEEDOR");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", proveedor.getCedula());
            params.put("p_nombre", proveedor.getNombre());
            params.put("p_apellidos", proveedor.getApellidos());
            params.put("p_email", proveedor.getEmail());
            params.put("p_telefono", proveedor.getTelefono());
            params.put("p_direccion", proveedor.getDireccion());

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar proveedor: " + e.getMessage());
        }
    }

    // Método para actualizar proveedor
    public void actualizarProveedor(Proveedor proveedor) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("ACTUALIZAR_PROVEEDOR");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", proveedor.getCedula());
            params.put("p_nombre", proveedor.getNombre());
            params.put("p_apellidos", proveedor.getApellidos());
            params.put("p_email", proveedor.getEmail());
            params.put("p_telefono", proveedor.getTelefono());
            params.put("p_direccion", proveedor.getDireccion());

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar proveedor: " + e.getMessage());
        }
    }

    // Método para deshabilitar proveedor
    public void deshabilitarProveedor(String cedula) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("DESHABILITAR_PROVEEDOR");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cedula);

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al deshabilitar proveedor: " + e.getMessage());
        }
    }

    // Método para habilitar proveedor
    public void habilitarProveedor(String cedula) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("HABILITAR_PROVEEDOR");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cedula);

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al habilitar proveedor: " + e.getMessage());
        }
    }

    // Método para obtener todos los proveedores
    public List<Proveedor> obtenerTodosLosProveedores() {
        String sql = "SELECT * FROM vista_proveedores";
        return jdbcTemplate.query(sql, proveedorRowMapper);
    }

    // Método para buscar proveedores
    public List<Proveedor> buscarProveedores(String busqueda) {
        String sql = "SELECT * FROM PROVEEDOR WHERE ESTADO = 'Activo' AND "
                + "(UPPER(NOMBRE) LIKE UPPER(?) OR UPPER(APELLIDOS) LIKE UPPER(?) OR CEDULA LIKE ?)";
        String parametroBusqueda = "%" + busqueda + "%";
        return jdbcTemplate.query(sql, proveedorRowMapper, parametroBusqueda, parametroBusqueda, parametroBusqueda);
    }

    // Método para obtener proveedor por ID
    public Proveedor obtenerProveedorPorId(Long id) {
        String sql = "SELECT * FROM PROVEEDOR WHERE ID_PROVEEDOR = ?";
        List<Proveedor> proveedores = jdbcTemplate.query(sql, proveedorRowMapper, id);
        return proveedores.isEmpty() ? null : proveedores.get(0);
    }

    // Método para obtener proveedor por cédula
    public Proveedor obtenerProveedorPorCedula(String cedula) {
        String sql = "SELECT * FROM PROVEEDOR WHERE CEDULA = ?";
        List<Proveedor> proveedores = jdbcTemplate.query(sql, proveedorRowMapper, cedula);
        return proveedores.isEmpty() ? null : proveedores.get(0);
    }
}