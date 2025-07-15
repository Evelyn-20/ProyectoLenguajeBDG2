package com.proyecto.Service;

import com.proyecto.Dao.ProductoDao;
import com.proyecto.Domain.Producto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoDao productoDao;

    // Obtener todos los productos
    public List<Producto> getProductos() {
        return productoDao.findAll();
    }

    // Obtener solo productos activos
    public List<Producto> getProductosActivos() {
        return productoDao.findProductosActivos();
    }

    // Buscar productos por nombre
    public List<Producto> buscarProductos(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return getProductos();
        }
        return productoDao.findByNombre(nombre.trim());
    }

    // Obtener producto por ID
    public Producto getProductoPorId(Long id) {
        return productoDao.findById(id);
    }

    // Consultar producto por código
    public Producto consultarProductoPorCodigo(String codigo) {
        return productoDao.consultarProducto(codigo);
    }

    // Registrar nuevo producto
    public void registrarProducto(Producto producto) {
        productoDao.registrarProducto(producto);
    }

    // Actualizar producto existente
    public void actualizarProducto(Producto producto) {
        productoDao.actualizarProducto(producto);
    }

    // Deshabilitar producto
    public void deshabilitarProducto(String codigo) {
        productoDao.deshabilitarProducto(codigo);
    }

    // Eliminar producto (deshabilitar)
    public void eliminarProducto(Long id) {
        Producto producto = productoDao.findById(id);
        if (producto != null) {
            productoDao.deshabilitarProducto(producto.getCodigo());
        }
    }
}