package com.proyecto.Service;

import com.proyecto.Domain.Asociacion;
import com.proyecto.Domain.Producto;
import com.proyecto.Domain.Proveedor;
import com.proyecto.Dao.AsociacionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AsociacionService {

    @Autowired
    private AsociacionDao asociacionDao;

    @Transactional
    public void registrarAsociacion(Asociacion asociacion) {
        // Validar que los datos requeridos estén presentes
        if (asociacion.getProductoId() == null || asociacion.getProveedorId() == null) {
            throw new IllegalArgumentException("Producto y Proveedor son obligatorios");
        }
        
        if (asociacion.getPrecioCompra() == null || asociacion.getPrecioCompra() <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor que cero");
        }

        // Verificar si ya existe una asociación activa
        if (asociacionDao.existeAsociacionActiva(asociacion.getProductoId(), asociacion.getProveedorId())) {
            throw new IllegalArgumentException("Ya existe una asociación activa entre este producto y proveedor");
        }
        
        asociacionDao.registrarAsociacion(asociacion);
    }

    @Transactional
    public void actualizarAsociacion(Asociacion asociacion) {
        // Validar que la asociación existe
        if (asociacion.getProductoProveedorId() == null) {
            throw new IllegalArgumentException("ID de asociación es obligatorio para actualizar");
        }
        
        // Validar datos requeridos
        if (asociacion.getProductoId() == null || asociacion.getProveedorId() == null) {
            throw new IllegalArgumentException("Producto y Proveedor son obligatorios");
        }
        
        if (asociacion.getPrecioCompra() == null || asociacion.getPrecioCompra() <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor que cero");
        }

        // Obtener la asociación actual para comparar
        Asociacion asociacionActual = asociacionDao.obtenerAsociacionPorId(asociacion.getProductoProveedorId());
        if (asociacionActual == null) {
            throw new IllegalArgumentException("Asociación no encontrada");
        }

        // Solo verificar duplicados si se cambió el producto o proveedor
        if (!asociacionActual.getProductoId().equals(asociacion.getProductoId()) || 
            !asociacionActual.getProveedorId().equals(asociacion.getProveedorId())) {
            
            if (asociacionDao.existeAsociacionActiva(asociacion.getProductoId(), asociacion.getProveedorId())) {
                throw new IllegalArgumentException("Ya existe otra asociación activa entre este producto y proveedor");
            }
        }
        
        asociacionDao.actualizarAsociacion(asociacion);
    }

    @Transactional
    public void deshabilitarAsociacion(Long asociacionId) {
        if (asociacionId == null) {
            throw new IllegalArgumentException("ID de asociación es obligatorio");
        }
        
        // Verificar que existe
        Asociacion asociacion = asociacionDao.obtenerAsociacionPorId(asociacionId);
        if (asociacion == null) {
            throw new IllegalArgumentException("Asociación no encontrada");
        }

        asociacionDao.deshabilitarAsociacion(asociacionId);
    }

    @Transactional
    public void habilitarAsociacion(Long asociacionId) {
        if (asociacionId == null) {
            throw new IllegalArgumentException("ID de asociación es obligatorio");
        }

        // Verificar que existe
        Asociacion asociacion = asociacionDao.obtenerAsociacionPorId(asociacionId);
        if (asociacion == null) {
            throw new IllegalArgumentException("Asociación no encontrada");
        }

        // Verificar que no existe otra asociación activa con el mismo producto y proveedor
        if (asociacionDao.existeAsociacionActiva(asociacion.getProductoId(), asociacion.getProveedorId())) {
            throw new IllegalArgumentException("Ya existe una asociación activa entre este producto y proveedor");
        }

        asociacionDao.habilitarAsociacion(asociacionId);
    }

    public List<Asociacion> obtenerAsociacionesActivas() {
        return asociacionDao.obtenerAsociacionesActivas();
    }

    public List<Asociacion> obtenerAsociacionesInactivas() {
        return asociacionDao.obtenerAsociacionesInactivas();
    }

    public List<Asociacion> obtenerTodasLasAsociaciones() {
        return asociacionDao.obtenerTodasLasAsociaciones();
    }

    public List<Asociacion> buscarAsociaciones(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerAsociacionesActivas();
        }
        return asociacionDao.buscarAsociaciones(busqueda);
    }

    public List<Asociacion> buscarTodasAsociaciones(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerTodasLasAsociaciones();
        }
        return asociacionDao.buscarAsociaciones(busqueda);
    }

    public Asociacion obtenerAsociacionPorId(Long id) {
        if (id == null) {
            return null;
        }
        return asociacionDao.obtenerAsociacionPorId(id);
    }

    // Métodos para obtener datos para los formularios
    public List<Producto> obtenerProductosActivos() {
        return asociacionDao.obtenerProductosActivos();
    }

    public List<Proveedor> obtenerProveedoresActivos() {
        return asociacionDao.obtenerProveedoresActivos();
    }

    // Consultar asociaciones por código de producto
    public List<Asociacion> consultarAsociacionPorCodigoProducto(String codigoProducto) {
        if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de producto es obligatorio");
        }
        return asociacionDao.consultarAsociacionPorCodigoProducto(codigoProducto);
    }

    // Métodos de utilidad
    public boolean existeAsociacion(Long productoId, Long proveedorId) {
        return asociacionDao.existeAsociacionActiva(productoId, proveedorId);
    }

    public int contarAsociacionesActivas() {
        return obtenerAsociacionesActivas().size();
    }

    public int contarAsociacionesPorProducto(Long productoId) {
        return (int) obtenerTodasLasAsociaciones().stream()
                .filter(a -> a.getProductoId().equals(productoId) 
                          && "Activo".equals(a.getEstado()))
                .count();
    }

    public int contarAsociacionesPorProveedor(Long proveedorId) {
        return (int) obtenerTodasLasAsociaciones().stream()
                .filter(a -> a.getProveedorId().equals(proveedorId) 
                          && "Activo".equals(a.getEstado()))
                .count();
    }
}