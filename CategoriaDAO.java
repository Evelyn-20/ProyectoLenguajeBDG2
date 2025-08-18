package com.proyecto.dao;

import com.proyecto.domain.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private Categoria mapRow(ResultSet rs) throws Exception {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombre(rs.getString("nombre_categoria"));
        categoria.setEstado(rs.getInt("estado"));
        return categoria;
    }

    public void registrarCategoria(Categoria c) {
        String sql = "INSERT INTO categorias (nombre_categoria, estado) VALUES (?, 1)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Categoria findById(int idCategoria) {
        Categoria categoria = null;
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoria;
    }

    public void actualizarCategoria(Categoria c) {
        String sql = "UPDATE categorias SET nombre_categoria = ? WHERE id_categoria = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getIdCategoria());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deshabilitarCategoria(int idCategoria) {
        String sql = "UPDATE categorias SET estado = 0 WHERE id_categoria = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Categoria> findAll() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE estado = 1 ORDER BY id_categoria";
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
