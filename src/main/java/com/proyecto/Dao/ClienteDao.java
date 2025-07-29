package com.proyecto.Repository;

import com.proyecto.Domain.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import oracle.jdbc.OracleTypes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class ClienteDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // RowMapper para convertir ResultSet a Cliente
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
            cliente.setContrasena(rs.getString("CONTRASENA"));
            cliente.setActivo("Activo".equals(rs.getString("ESTADO")));
            return cliente;
        }
    };
    
    // Método para registrar cliente usando procedimiento almacenado
    public void registrarCliente(Cliente cliente) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("registrar_cliente");
            
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
    
    // Método para actualizar cliente usando procedimiento almacenado
    public void actualizarCliente(Cliente cliente) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("actualizar_cliente");
            
            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cliente.getCedula());
            params.put("p_nombre", cliente.getNombre());
            params.put("p_apellidos", cliente.getApellidos());
            params.put("p_email", cliente.getEmail());
            params.put("p_telefono", cliente.getTelefono());
            params.put("p_direccion", cliente.getDireccion());
            
            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage());
        }
    }
    
    // Método para deshabilitar cliente usando procedimiento almacenado
    public void deshabilitarCliente(String cedula) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("deshabilitar_cliente");
            
            Map<String, Object> params = new HashMap<>();
            params.put("p_cedula", cedula);
            
            jdbcCall.execute(params);
        } catch (Exception e) {
            throw new RuntimeException("Error al deshabilitar cliente: " + e.getMessage());
        }
    }
    
    // Método para obtener todos los clientes usando vista
    public List<Cliente> obtenerClientesActivos() {
        String sql = "SELECT * FROM CLIENTE WHERE ESTADO = 'Activo'";
        return jdbcTemplate.query(sql, clienteRowMapper);
    }
    
    // Método para buscar clientes por nombre o cédula
    public List<Cliente> buscarClientes(String busqueda) {
        String sql = "SELECT * FROM CLIENTE WHERE ESTADO = 'Activo' AND " +
                    "(UPPER(NOMBRE) LIKE UPPER(?) OR UPPER(APELLIDOS) LIKE UPPER(?) OR CEDULA LIKE ?)";
        String parametroBusqueda = "%" + busqueda + "%";
        return jdbcTemplate.query(sql, clienteRowMapper, parametroBusqueda, parametroBusqueda, parametroBusqueda);
    }
    
    // Método para obtener cliente por ID
    public Cliente obtenerClientePorId(Long id) {
        String sql = "SELECT * FROM CLIENTE WHERE ID_CLIENTE = ?";
        List<Cliente> clientes = jdbcTemplate.query(sql, clienteRowMapper, id);
        return clientes.isEmpty() ? null : clientes.get(0);
    }
    
    // Método para obtener cliente por cédula
    public Cliente obtenerClientePorCedula(String cedula) {
        String sql = "SELECT * FROM CLIENTE WHERE CEDULA = ?";
        List<Cliente> clientes = jdbcTemplate.query(sql, clienteRowMapper, cedula);
        return clientes.isEmpty() ? null : clientes.get(0);
    }
    
    // Método usando vista para clientes activos
    public List<Cliente> obtenerClientesActivosVista() {
        String sql = "SELECT c.*, 'Activo' as ESTADO FROM vista_clientes_activos c " +
                    "JOIN CLIENTE cl ON c.CEDULA = cl.CEDULA";
        return jdbcTemplate.query(sql, clienteRowMapper);
    }
}