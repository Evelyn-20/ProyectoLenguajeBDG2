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
import java.sql.Types;
import java.util.Collections;
import org.springframework.jdbc.core.SqlOutParameter;

@Repository
public class ProductoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper completo para consultas con JOIN (incluye categoría)
    private RowMapper<Producto> productoCompletoRowMapper = (rs, rowNum) -> {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getLong("ID_PRODUCTO"));
        producto.setCodigo(rs.getString("CODIGO_PRODUCTO"));
        producto.setNombre(rs.getString("NOMBRE_PRODUCTO"));
        producto.setDescripcion(rs.getString("DESRIPCION_PRODUCTO"));
        producto.setMaterial(rs.getString("MATERIAL"));
        producto.setCategoria(rs.getString("NOMBRE_CATEGORIA"));
        producto.setCategoriaId(rs.getLong("ID_CATEGORIA"));

        String imagen = rs.getString("IMAGEN");
        producto.setRutaImagen(imagen != null ? imagen : "default-image.jpg");

        producto.setActivo("Activo".equals(rs.getString("ESTADO")));

        return producto;
    };

    // Método principal usando procedimiento almacenado listar_productos_por_categoria
    public List<Producto> findByCategoriaId(Long categoriaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("listar_productos_por_categoria")
                .declareParameters(
                        new SqlParameter("p_categoria_id", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.REF_CURSOR, productoCompletoRowMapper)
                )
                .withoutProcedureColumnMetaDataAccess(); // Importante para mejor rendimiento

        Map<String, Object> inParams = Map.of("p_categoria_id", categoriaId);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            @SuppressWarnings("unchecked")
            List<Producto> productos = (List<Producto>) result.get("p_result");
            return productos != null ? productos : Collections.emptyList();
        } catch (Exception e) {

            return Collections.emptyList();
        }
    }

    private String obtenerNombreCategoria(Long categoriaId) {
        try {
            String sql = "SELECT NOMBRE_CATEGORIA FROM CATEGORIA WHERE ID_CATEGORIA = ?";
            return jdbcTemplate.queryForObject(sql, String.class, categoriaId);
        } catch (Exception e) {
            return "Categoría no encontrada";
        }
    }

    public List<Producto> findAll() {
        String sql = """
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
                   p.ESTADO, p.IMAGEN, c.ID_CATEGORIA
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            ORDER BY p.ID_PRODUCTO
        """;
        return jdbcTemplate.query(sql, productoCompletoRowMapper);
    }

    // Buscar productos por nombre usando procedimiento almacenado
    public List<Producto> findByNombre(String nombre) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("buscar_producto_por_nombre")
                .declareParameters(
                        new SqlParameter("p_nombre", Types.VARCHAR),
                        new SqlParameter("p_result", Types.REF_CURSOR)
                );

        Map<String, Object> inParams = Map.of("p_nombre", nombre);
        Map<String, Object> result = jdbcCall.execute(inParams);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_result");

        return rows.stream().map(row -> {
            Producto producto = new Producto();

            // Manejo seguro de ID_PRODUCTO
            Object idObj = row.get("ID_PRODUCTO");
            if (idObj != null) {
                producto.setIdProducto(((Number) idObj).longValue());
            }

            producto.setCodigo((String) row.get("CODIGO_PRODUCTO"));
            producto.setNombre((String) row.get("NOMBRE_PRODUCTO"));
            producto.setDescripcion((String) row.get("DESRIPCION_PRODUCTO"));
            producto.setMaterial((String) row.get("MATERIAL"));

            // Manejar imagen
            String imagen = (String) row.get("IMAGEN");
            producto.setRutaImagen(imagen != null ? imagen : "default-image.jpg");

            producto.setActivo("Activo".equals(row.get("ESTADO")));

            // Para búsquedas, obtener información de categoría si está disponible
            Object categoriaIdObj = row.get("ID_CATEGORIA");
            if (categoriaIdObj != null) {
                Long categoriaId = ((Number) categoriaIdObj).longValue();
                producto.setCategoriaId(categoriaId);
                producto.setCategoria(obtenerNombreCategoria(categoriaId));
            }

            return producto;
        }).toList();
    }

    // Buscar producto por ID usando JOIN
    public Producto findById(Long id) {
        String sql = """
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
                   p.ESTADO, p.IMAGEN, c.ID_CATEGORIA
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            WHERE p.ID_PRODUCTO = ?
        """;
        List<Producto> productos = jdbcTemplate.query(sql, productoCompletoRowMapper, id);
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
                        new SqlParameter("p_categoria_id", Types.NUMERIC),
                        new SqlParameter("p_imagen", Types.VARCHAR)
                );

        Map<String, Object> inParams = Map.of(
                "p_codigo", producto.getCodigo(),
                "p_nombre", producto.getNombre(),
                "p_descripcion", producto.getDescripcion(),
                "p_material", producto.getMaterial(),
                "p_categoria_id", producto.getCategoriaId(),
                "p_imagen", producto.getRutaImagen() != null ? producto.getRutaImagen() : "default-image.jpg"
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
                        new SqlParameter("p_categoria_id", Types.NUMERIC),
                        new SqlParameter("p_imagen", Types.VARCHAR)
                );

        Map<String, Object> inParams = Map.of(
                "p_codigo", producto.getCodigo(),
                "p_nombre", producto.getNombre(),
                "p_descripcion", producto.getDescripcion(),
                "p_material", producto.getMaterial(),
                "p_categoria_id", producto.getCategoriaId(),
                "p_imagen", producto.getRutaImagen() != null ? producto.getRutaImagen() : "default-image.jpg"
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
        producto.setDescripcion((String) row.get("DESRIPCION_PRODUCTO"));
        producto.setMaterial((String) row.get("MATERIAL"));

        // Manejar imagen
        String imagen = (String) row.get("IMAGEN");
        producto.setRutaImagen(imagen != null ? imagen : "default-image.jpg");

        producto.setActivo("Activo".equals(row.get("ESTADO")));

        // Obtener información de categoría si está disponible
        Object categoriaIdObj = row.get("ID_CATEGORIA");
        if (categoriaIdObj != null) {
            Long categoriaId = ((Number) categoriaIdObj).longValue();
            producto.setCategoriaId(categoriaId);
            producto.setCategoria(obtenerNombreCategoria(categoriaId));
        }

        return producto;
    }

    // Obtener productos activos
    public List<Producto> findProductosActivos() {
        String sql = """
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
                   p.ESTADO, p.IMAGEN, c.ID_CATEGORIA
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            WHERE p.ESTADO = 'Activo'
            ORDER BY p.ID_PRODUCTO
        """;
        return jdbcTemplate.query(sql, productoCompletoRowMapper);
    }
}