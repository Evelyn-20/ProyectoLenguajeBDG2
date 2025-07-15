package com.proyecto.Dao;

import com.proyecto.Domain.Producto;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Repository
public class ProductoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper para mapear resultados de la base de datos al objeto Producto
    private RowMapper<Producto> productoRowMapper = new RowMapper<Producto>() {
        @Override
        public Producto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Producto producto = new Producto();
            producto.setIdProducto(rs.getLong("ID_PRODUCTO"));
            producto.setCodigo(rs.getString("CODIGO_PRODUCTO"));
            producto.setNombre(rs.getString("NOMBRE_PRODUCTO"));
            producto.setDescripcion(rs.getString("DESRIPCION_PRODUCTO"));
            producto.setMaterial(rs.getString("MATERIAL"));
            producto.setCategoria(rs.getString("NOMBRE_CATEGORIA"));
            producto.setActivo("Activo".equals(rs.getString("ESTADO")));
            return producto;
        }
    };

    // Listar todos los productos usando la vista
    public List<Producto> findAll() {
        String sql = """
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, p.ESTADO
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            ORDER BY p.ID_PRODUCTO
        """;
        return jdbcTemplate.query(sql, productoRowMapper);
    }

    // Buscar productos por nombre usando la vista
    public List<Producto> findByNombre(String nombre) {
        String sql = """
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, p.ESTADO
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            WHERE UPPER(p.NOMBRE_PRODUCTO) LIKE UPPER(?)
            ORDER BY p.ID_PRODUCTO
        """;
        return jdbcTemplate.query(sql, productoRowMapper, "%" + nombre + "%");
    }

    // Buscar producto por ID
    public Producto findById(Long id) {
        String sql = """
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, p.ESTADO
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            WHERE p.ID_PRODUCTO = ?
        """;
        List<Producto> productos = jdbcTemplate.query(sql, productoRowMapper, id);
        return productos.isEmpty() ? null : productos.get(0);
    }

    // Registrar producto usando procedimiento almacenado
    public void registrarProducto(Producto producto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("registrar_producto")
                .declareParameters(
                        new SqlParameter("p_codigo", Types.VARCHAR),
                        new SqlParameter("p_nombre", Types.VARCHAR),
                        new SqlParameter("p_descripcion", Types.VARCHAR),
                        new SqlParameter("p_material", Types.VARCHAR),
                        new SqlParameter("p_categoria_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = Map.of(
                "p_codigo", producto.getCodigo(),
                "p_nombre", producto.getNombre(),
                "p_descripcion", producto.getDescripcion(),
                "p_material", producto.getMaterial(),
                "p_categoria_id", producto.getCategoriaId()
        );

        jdbcCall.execute(inParams);
    }

    // Actualizar producto usando procedimiento almacenado
    public void actualizarProducto(Producto producto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("actualizar_producto")
                .declareParameters(
                        new SqlParameter("p_codigo", Types.VARCHAR),
                        new SqlParameter("p_nombre", Types.VARCHAR),
                        new SqlParameter("p_descripcion", Types.VARCHAR),
                        new SqlParameter("p_material", Types.VARCHAR),
                        new SqlParameter("p_categoria_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = Map.of(
                "p_codigo", producto.getCodigo(),
                "p_nombre", producto.getNombre(),
                "p_descripcion", producto.getDescripcion(),
                "p_material", producto.getMaterial(),
                "p_categoria_id", producto.getCategoriaId()
        );

        jdbcCall.execute(inParams);
    }

    // Deshabilitar producto usando procedimiento almacenado
    public void deshabilitarProducto(String codigo) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("deshabilitar_producto")
                .declareParameters(
                        new SqlParameter("p_codigo", Types.VARCHAR)
                );

        Map<String, Object> inParams = Map.of("p_codigo", codigo);
        jdbcCall.execute(inParams);
    }

    // Consultar producto usando procedimiento almacenado
    public Producto consultarProducto(String codigo) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("consultar_producto")
                .declareParameters(
                        new SqlParameter("p_codigo", Types.VARCHAR),
                        new SqlParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = Map.of("p_codigo", codigo);
        Map<String, Object> result = jdbcCall.execute(inParams);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");
        
        if (rows.isEmpty()) {
            return null;
        }
        
        Map<String, Object> row = rows.get(0);
        Producto producto = new Producto();
        producto.setIdProducto(((Number) row.get("ID_PRODUCTO")).longValue());
        producto.setCodigo((String) row.get("CODIGO_PRODUCTO"));
        producto.setNombre((String) row.get("NOMBRE_PRODUCTO"));
        producto.setDescripcion((String) row.get("DESCRIPCION_PRODUCTO"));
        producto.setMaterial((String) row.get("MATERIAL"));
        producto.setActivo("Activo".equals(row.get("ESTADO")));
        
        return producto;
    }

    // Obtener productos activos usando la vista
    public List<Producto> findProductosActivos() {
        String sql = "SELECT * FROM vista_productos_activos";
        return jdbcTemplate.query(sql, new RowMapper<Producto>() {
            @Override
            public Producto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Producto producto = new Producto();
                producto.setCodigo(rs.getString("CODIGO_PRODUCTO"));
                producto.setNombre(rs.getString("NOMBRE_PRODUCTO"));
                producto.setDescripcion(rs.getString("DESCRIPCION_PRODUCTO"));
                producto.setMaterial(rs.getString("MATERIAL"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setActivo("Activo".equals(rs.getString("ESTADO")));
                return producto;
            }
        });
    }
}