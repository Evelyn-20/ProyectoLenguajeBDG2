package com.proyecto.Service;

import com.proyecto.Domain.Proveedor;
import com.proyecto.Dao.ProveedorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorDao proveedorDao;

    @Transactional
    public void registrarProveedor(Proveedor proveedor) {
        proveedorDao.registrarProveedor(proveedor);
    }

    @Transactional
    public void actualizarProveedor(Proveedor proveedor) {
        proveedorDao.actualizarProveedor(proveedor);
    }

    @Transactional
    public void deshabilitarProveedor(String cedula) {
        proveedorDao.deshabilitarProveedor(cedula);
    }

    @Transactional
    public void habilitarProveedor(String cedula) {
        proveedorDao.habilitarProveedor(cedula);
    }

    public List<Proveedor> obtenerProveedoresActivos() {
        return proveedorDao.obtenerProveedoresActivos();
    }

    public List<Proveedor> obtenerProveedoresInactivos() {
        return proveedorDao.obtenerProveedoresInactivos();
    }

    public List<Proveedor> obtenerTodosLosProveedores() {
        return proveedorDao.obtenerTodosLosProveedores();
    }

    public List<Proveedor> buscarProveedores(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerProveedoresActivos();
        }
        return proveedorDao.buscarProveedores(busqueda);
    }

    public List<Proveedor> buscarTodosProveedores(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerTodosLosProveedores();
        }
        return proveedorDao.buscarTodosProveedores(busqueda);
    }

    public Proveedor obtenerProveedorPorId(Long id) {
        return proveedorDao.obtenerProveedorPorId(id);
    }

    public Proveedor obtenerProveedorPorCedula(String cedula) {
        return proveedorDao.obtenerProveedorPorCedula(cedula);
    }

    public boolean proveedorTieneProductos(Long idProveedor) {
        return proveedorDao.proveedorTieneProductos(idProveedor);
    }
}