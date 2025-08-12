package com.proyecto.Dao;

import com.proyecto.Domain.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class ClienteDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Cliente> clienteRowMapper = new RowMapper<Cliente>() {
        @Override
        public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(rs.getLong("ID_CLIENTE"));
            cliente.setCedula(rs.getString("CEDULA"));
            cliente.setNombre(rs.getString("NOMBRE"));
            cliente.setApellidos(rs.getString("APELLIDOS"));
            cliente.setEmail(rs.getString("EMAIL"));
            cliente.setTelefono(rs.getString("TELEFONO"));
            cliente.setDireccion(rs.getString("DIRECCION"));
            cliente.setEstado(rs.getString("ESTADO"));
            return cliente;
        }
    };

    public void registrarCliente(Cliente cliente) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("REGISTRAR_CLIENTE");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cliente.getCedula());
            params.put("p_nombre", cliente.getNombre());
            params.put("p_apellidos", cliente.getApellidos());
            params.put("p_email", cliente.getEmail());
            params.put("p_telefono", cliente.getTelefono());
            params.put("p_direccion", cliente.getDireccion());
            params.put("p_password", cliente.getContrasena());

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar cliente: " + e.getMessage());
        }
    }

    public void actualizarCliente(Cliente cliente, String nuevaContrasena) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("ACTUALIZAR_CLIENTE");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cliente.getCedula());
            params.put("p_nombre", cliente.getNombre());
            params.put("p_apellidos", cliente.getApellidos());
            params.put("p_email", cliente.getEmail());
            params.put("p_telefono", cliente.getTelefono());
            params.put("p_direccion", cliente.getDireccion());

            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                params.put("p_contrasena", nuevaContrasena);
            }

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage());
        }
    }
    
    public void modificarCliente(Cliente cliente) {
        actualizarCliente(cliente, null);
    }

    public void deshabilitarCliente(String cedula) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("DESHABILITAR_CLIENTE");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cedula);

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al deshabilitar cliente: " + e.getMessage());
        }
    }

    public List<Cliente> buscarClientes(String busqueda) {
        String sql = "SELECT * FROM CLIENTE WHERE " +
                     "(UPPER(NOMBRE) LIKE UPPER(?) OR " +
                     "UPPER(APELLIDOS) LIKE UPPER(?) OR " +
                     "CEDULA LIKE ?)";
        String parametroBusqueda = "%" + busqueda + "%";
        return jdbcTemplate.query(sql, clienteRowMapper, parametroBusqueda, parametroBusqueda, parametroBusqueda);
    }

    public Cliente obtenerClientePorId(Long id) {
        String sql = "SELECT * FROM CLIENTE WHERE ID_CLIENTE = ?";
        List<Cliente> clientes = jdbcTemplate.query(sql, clienteRowMapper, id);
        return clientes.isEmpty() ? null : clientes.get(0);
    }

    public Cliente obtenerClientePorCedula(String cedula) {
        String sql = "SELECT * FROM CLIENTE WHERE CEDULA = ?";
        List<Cliente> clientes = jdbcTemplate.query(sql, clienteRowMapper, cedula);
        return clientes.isEmpty() ? null : clientes.get(0);
    }

    public List<Cliente> obtenerClientesActivos() {
        String sql = "SELECT * FROM vista_clientes_activos";
        return jdbcTemplate.query(sql, clienteRowMapper);
    }

    public List<Cliente> obtenerClientesInactivos() {
        String sql = "SELECT * FROM vista_clientes_inactivos";
        return jdbcTemplate.query(sql, clienteRowMapper);
    }

    public List<Cliente> obtenerTodosLosClientes() {
        String sql = "SELECT * FROM CLIENTE";
        return jdbcTemplate.query(sql, clienteRowMapper);
    }

    public List<Cliente> buscarTodosClientes(String busqueda) {
        String sql = "SELECT * FROM CLIENTE WHERE " +
                     "(UPPER(NOMBRE) LIKE UPPER(?) OR " +
                     "UPPER(APELLIDOS) LIKE UPPER(?) OR " +
                     "CEDULA LIKE ?)";
        String parametroBusqueda = "%" + busqueda + "%";
        return jdbcTemplate.query(sql, clienteRowMapper, parametroBusqueda, parametroBusqueda, parametroBusqueda);
    }

    public void activarCliente(String cedula) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("HABILITAR_CLIENTE");

            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cedula);

            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al habilitar cliente: " + e.getMessage());
        }
    }

    // Nueva función para verificar si un email existe
    public boolean existeEmail(String email) {
        String sql = "SELECT fn_existe_cliente_email(?) FROM dual";
        return jdbcTemplate.queryForObject(sql, Boolean.class, email);
    }

    // Nueva función para verificar si un cliente está activo
    public boolean verificarUsuarioActivo(String email) {
        String sql = "SELECT fn_verificar_usuario_activo(?) FROM dual";
        return jdbcTemplate.queryForObject(sql, Boolean.class, email);
    }
}