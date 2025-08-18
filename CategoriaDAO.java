package com.proyecto.dao;

import com.proyecto.domain.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    
    public void registrarCategoria(Categoria c) {
        String sql = "INSERT INTO categorias(nombre_categoria, estado) VALUES(?, 1)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreCategoria());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Categoria consultarCategoria(int idCategoria) {
        Categoria c = null;
        String sql = "SELECT * FROM categorias WHERE id_categoria=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                c.setEstado(rs.getInt("estado"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void actualizarCategoria(Categoria c) {
        String sql = "UPDATE categorias SET nombre_categoria=? WHERE id_categoria=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreCategoria());
            ps.setInt(2, c.getIdCategoria());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deshabilitarCategoria(int idCategoria) {
        String sql = "UPDATE categorias SET estado=0 WHERE id_categoria=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE estado=1 ORDER BY nombre_categoria";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                c.setEstado(rs.getInt("estado"));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
