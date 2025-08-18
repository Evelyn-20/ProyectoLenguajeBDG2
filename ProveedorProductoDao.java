package com.proyecto.dao;

import com.proyecto.domain.ProveedorProducto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProveedorProductoDAO {


    private ProveedorProducto mapRow(ResultSet rs) throws Exception {
        ProveedorProducto pp = new ProveedorProducto();
        pp.setIdProveedor(rs.getInt("id_proveedor"));
        pp.setNombreProveedor(rs.getString("nombre_proveedor"));
        pp.setIdProducto(rs.getInt("id_producto"));
        pp.setNombreProducto(rs.getString("nombre_producto"));
        pp.setEstado(rs.getInt("estado"));
        return pp;
    }

    public void registrarAsociacion(int idProveedor, int idProducto) {
        String sql = "INSERT INTO proveedor_producto (id_proveedor, id_producto, estado) VALUES (?, ?, 1)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ProveedorProducto findById(int idProveedor, int idProducto) {
        ProveedorProducto pp = null;
        String sql = "SELECT pp.id_proveedor, pr.nombre_proveedor, pp.id_producto, p.nombre_producto, pp.estado " +
                     "FROM proveedor_producto pp " +
                     "JOIN proveedores pr ON pr.id_proveedor = pp.id_proveedor " +
                     "JOIN productos p ON p.id_producto = pp.id_producto " +
                     "WHERE pp.id_proveedor = ? AND pp.id_producto = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pp = mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pp;
    }


    public void actualizarAsociacion(int idProveedor, int idProducto, int nuevoIdProducto) {
        String sql = "UPDATE proveedor_producto SET id_producto = ? WHERE id_proveedor = ? AND id_producto = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoIdProducto);
            ps.setInt(2, idProveedor);
            ps.setInt(3, idProducto);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deshabilitarAsociacion(int idProveedor, int idProducto) {
        String sql = "UPDATE proveedor_producto SET estado = 0 WHERE id_proveedor = ? AND id_producto = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProveedorProducto> findAll() {
        List<ProveedorProducto> lista = new ArrayList<>();
        String sql = "SELECT pp.id_proveedor, pr.nombre_proveedor, pp.id_producto, p.nombre_producto, pp.estado " +
                     "FROM proveedor_producto pp " +
                     "JOIN proveedores pr ON pr.id_proveedor = pp.id_proveedor " +
                     "JOIN productos p ON p.id_producto = pp.id_producto " +
                     "WHERE pp.estado = 1 " +
                     "ORDER BY pr.nombre_proveedor, p.nombre_producto";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
