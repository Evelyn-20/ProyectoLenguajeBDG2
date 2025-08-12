package com.proyecto.Dao;

import com.proyecto.Domain.Categoria;
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
public class CategoriaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper básico para categorías
    private RowMapper<Categoria> categoriaRowMapper = new RowMapper<Categoria>() {
        @Override
        public Categoria mapRow(ResultSet rs, int rowNum) throws SQLException {
            Categoria categoria = new Categoria();
            categoria.setCategoriaId(rs.getLong("ID_CATEGORIA"));
            categoria.setCodigoCategoria(rs.getString("CODIGO_CATEGORIA"));
            categoria.setNombreCategoria(rs.getString("NOMBRE_CATEGORIA"));
            categoria.setDescripcionCategoria(rs.getString("DESRIPCION_CATEGORIA"));
            categoria.setEstado(rs.getString("ESTADO"));
            return categoria;
        }
    };

    // Obtener categorías por rango de códigos con primera imagen de producto
    private List<Categoria> findCategoriasPorRango(int codigoInicio, int codigoFin) {
        String sql = """
            SELECT c.ID_CATEGORIA, c.CODIGO_CATEGORIA, c.NOMBRE_CATEGORIA, 
                   c.DESRIPCION_CATEGORIA, c.ESTADO
            FROM CATEGORIA c
            WHERE c.CODIGO_CATEGORIA BETWEEN ? AND ?
            AND c.ESTADO = 'Activo'
            ORDER BY c.CODIGO_CATEGORIA
        """;
        
        List<Categoria> categorias = jdbcTemplate.query(sql, categoriaRowMapper, codigoInicio, codigoFin);
        
        // Obtener la primera imagen de producto para cada categoría
        categorias.forEach(categoria -> {
            String imagen = obtenerPrimeraImagenProducto(categoria.getCategoriaId());
            categoria.setImagenProducto(imagen);
        });
        
        return categorias;
    }

    // Método para obtener la primera imagen de producto de una categoría
    private String obtenerPrimeraImagenProducto(Long categoriaId) {
        try {
            String sql = """
                SELECT p.IMAGEN 
                FROM PRODUCTO p
                WHERE p.ID_CATEGORIA = ? 
                AND p.ESTADO = 'Activo'
                AND p.IMAGEN IS NOT NULL
                AND ROWNUM = 1
                ORDER BY p.ID_PRODUCTO
            """;
            
            String imagen = jdbcTemplate.queryForObject(sql, String.class, categoriaId);
            return imagen != null ? imagen : "default-image.jpg";
        } catch (Exception e) {
            return "default-image.jpg";
        }
    }

    // Obtener subcategorías de mujer (códigos 210-250)
    public List<Categoria> findSubcategoriasMujer() {
        return findCategoriasPorRango(210, 250);
    }

    // Obtener subcategorías de hombre (códigos 110-130)
    public List<Categoria> findSubcategoriasHombre() {
        return findCategoriasPorRango(110, 130);
    }

    // Obtener subcategorías de zapatos (códigos 310-350)
    public List<Categoria> findSubcategoriasZapatos() {
        return findCategoriasPorRango(310, 350);
    }

    // Obtener subcategorías de accesorios (códigos 410-460)
    public List<Categoria> findSubcategoriasAccesorios() {
        return findCategoriasPorRango(410, 460);
    }

    // Obtener todas las categorías principales (códigos base: 100, 200, 300, 400)
    public List<Categoria> findCategoriasPrincipales() {
        String sql = """
            SELECT c.ID_CATEGORIA, c.CODIGO_CATEGORIA, c.NOMBRE_CATEGORIA, 
                   c.DESRIPCION_CATEGORIA, c.ESTADO
            FROM CATEGORIA c
            WHERE c.CODIGO_CATEGORIA IN (100, 200, 300, 400)
            AND c.ESTADO = 'Activo'
            ORDER BY c.CODIGO_CATEGORIA
        """;
        
        List<Categoria> categorias = jdbcTemplate.query(sql, categoriaRowMapper);
        
        // Para las categorías principales, usar imagen por defecto
        categorias.forEach(categoria -> {
            categoria.setImagenProducto("default-image.jpg");
        });
        
        return categorias;
    }

    // Obtener todas las categorías activas
    public List<Categoria> findAll() {
        String sql = """
            SELECT c.ID_CATEGORIA, c.CODIGO_CATEGORIA, c.NOMBRE_CATEGORIA, 
                   c.DESRIPCION_CATEGORIA, c.ESTADO
            FROM CATEGORIA c
            WHERE c.ESTADO = 'Activo'
            ORDER BY c.CODIGO_CATEGORIA
        """;
        
        List<Categoria> categorias = jdbcTemplate.query(sql, categoriaRowMapper);
        
        // Obtener la primera imagen de producto para cada categoría
        categorias.forEach(categoria -> {
            String imagen = obtenerPrimeraImagenProducto(categoria.getCategoriaId());
            categoria.setImagenProducto(imagen);
        });
        
        return categorias;
    }

    // Buscar categoría por ID
    public Categoria findById(Long id) {
        String sql = """
            SELECT c.ID_CATEGORIA, c.CODIGO_CATEGORIA, c.NOMBRE_CATEGORIA, 
                   c.DESRIPCION_CATEGORIA, c.ESTADO
            FROM CATEGORIA c
            WHERE c.ID_CATEGORIA = ?
        """;
        
        List<Categoria> categorias = jdbcTemplate.query(sql, categoriaRowMapper, id);
        
        if (categorias.isEmpty()) {
            return null;
        }
        
        Categoria categoria = categorias.get(0);
        String imagen = obtenerPrimeraImagenProducto(categoria.getCategoriaId());
        categoria.setImagenProducto(imagen);
        
        return categoria;
    }

    // Usar procedimiento almacenado para registrar categoría
    public void registrarCategoria(Categoria categoria) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("registrar_categoria")
                .declareParameters(
                        new SqlParameter("p_nombre", Types.VARCHAR),
                        new SqlParameter("p_descripcion", Types.VARCHAR)
                );

        Map<String, Object> inParams = Map.of(
                "p_nombre", categoria.getNombreCategoria(),
                "p_descripcion", categoria.getDescripcionCategoria()
        );

        jdbcCall.execute(inParams);
    }

    // Usar procedimiento almacenado para actualizar categoría
    public void actualizarCategoria(Categoria categoria) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("actualizar_categoria")
                .declareParameters(
                        new SqlParameter("p_categoria_id", Types.NUMERIC),
                        new SqlParameter("p_nombre", Types.VARCHAR),
                        new SqlParameter("p_descripcion", Types.VARCHAR)
                );

        Map<String, Object> inParams = Map.of(
                "p_categoria_id", categoria.getCategoriaId(),
                "p_nombre", categoria.getNombreCategoria(),
                "p_descripcion", categoria.getDescripcionCategoria()
        );

        jdbcCall.execute(inParams);
    }

    // Usar procedimiento almacenado para deshabilitar categoría
    public void deshabilitarCategoria(Long categoriaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("deshabilitar_categoria")
                .declareParameters(
                        new SqlParameter("p_categoria_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = Map.of("p_categoria_id", categoriaId);
        jdbcCall.execute(inParams);
    }

    // Usar procedimiento almacenado para habilitar categoría
    public void habilitarCategoria(Long categoriaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("habilitar_categoria")
                .declareParameters(
                        new SqlParameter("p_categoria_id", Types.NUMERIC)
                );

        Map<String, Object> inParams = Map.of("p_categoria_id", categoriaId);
        jdbcCall.execute(inParams);
    }

    // Método para obtener el mapeo de URL para cada subcategoría
    public String obtenerUrlCategoria(Long categoriaId) {
        switch (categoriaId.intValue()) {
            // Categorías de Mujer
            case 8: return "vestidos";
            case 9: return "blusas-camisetas";
            case 10: return "faldas";
            case 11: return "pantalones-mujer";
            case 12: return "chaquetas-sudaderas-mujer";
            // Categorías de Hombre
            case 5: return "camisas-camisetas";
            case 6: return "pantalones-hombre";
            case 7: return "chaquetas-sudaderas-hombre";
            // Categorías de Zapatos
            case 13: return "tenis";
            case 14: return "botas";
            case 15: return "zapatos-formales";
            case 16: return "sandalias";
            case 17: return "zapatos-casuales";
            // Categorías de Accesorios
            case 18: return "bolso-mochila";
            case 19: return "joyería";
            case 20: return "gorras-sombreros";
            case 21: return "cinturones";
            case 22: return "lentes";
            case 23: return "bufandas-guantes";
            default: return "categoria-" + categoriaId;
        }
    }
}