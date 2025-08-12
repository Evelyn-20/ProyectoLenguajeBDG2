package com.proyecto.Service;

import com.proyecto.Dao.CategoriaDao;
import com.proyecto.Domain.Categoria;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaDao categoriaDao;

    // Obtener subcategorías de mujer con imágenes
    public List<Categoria> getSubcategoriasMujer() {
        List<Categoria> categorias = categoriaDao.findSubcategoriasMujer();
        // Agregar URLs dinámicas para cada categoría
        categorias.forEach(categoria -> {
            categoria.setCodigoUrl(categoriaDao.obtenerUrlCategoria(categoria.getCategoriaId()));
        });
        return categorias;
    }

    // Obtener subcategorías de hombre con imágenes
    public List<Categoria> getSubcategoriasHombre() {
        List<Categoria> categorias = categoriaDao.findSubcategoriasHombre();
        categorias.forEach(categoria -> {
            categoria.setCodigoUrl(categoriaDao.obtenerUrlCategoria(categoria.getCategoriaId()));
        });
        return categorias;
    }

    // Obtener subcategorías de zapatos con imágenes
    public List<Categoria> getSubcategoriasZapatos() {
        List<Categoria> categorias = categoriaDao.findSubcategoriasZapatos();
        categorias.forEach(categoria -> {
            categoria.setCodigoUrl(categoriaDao.obtenerUrlCategoria(categoria.getCategoriaId()));
        });
        return categorias;
    }

    // Obtener subcategorías de accesorios con imágenes
    public List<Categoria> getSubcategoriasAccesorios() {
        List<Categoria> categorias = categoriaDao.findSubcategoriasAccesorios();
        categorias.forEach(categoria -> {
            categoria.setCodigoUrl(categoriaDao.obtenerUrlCategoria(categoria.getCategoriaId()));
        });
        return categorias;
    }

    // Obtener todas las categorías
    public List<Categoria> getCategorias() {
        return categoriaDao.findAll();
    }

    // Obtener categorías principales
    public List<Categoria> getCategoriasPrincipales() {
        return categoriaDao.findCategoriasPrincipales();
    }

    // Obtener categoría por ID
    public Categoria getCategoriaPorId(Long id) {
        Categoria categoria = categoriaDao.findById(id);
        if (categoria != null) {
            categoria.setCodigoUrl(categoriaDao.obtenerUrlCategoria(categoria.getCategoriaId()));
        }
        return categoria;
    }

    // Métodos CRUD usando procedimientos almacenados
    public void registrarCategoria(Categoria categoria) {
        categoriaDao.registrarCategoria(categoria);
    }

    public void actualizarCategoria(Categoria categoria) {
        categoriaDao.actualizarCategoria(categoria);
    }

    public void deshabilitarCategoria(Long categoriaId) {
        categoriaDao.deshabilitarCategoria(categoriaId);
    }

    public void habilitarCategoria(Long categoriaId) {
        categoriaDao.habilitarCategoria(categoriaId);
    }
}