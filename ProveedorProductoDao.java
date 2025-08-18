import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorProductoDAO {

    public void registrar(int idProveedor, int idProducto) {
        String sql = "INSERT INTO proveedor_producto(id_proveedor, id_producto, estado) VALUES(?, ?, 1)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean consultar(int idProveedor, int idProducto) {
        String sql = "SELECT * FROM proveedor_producto WHERE id_proveedor=? AND id_producto=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void actualizar(int idProveedor, int idProducto, int nuevoIdProducto) {
        String sql = "UPDATE proveedor_producto SET id_producto=? WHERE id_proveedor=? AND id_producto=?";
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

    
    public void deshabilitar(int idProveedor, int idProducto) {
        String sql = "UPDATE proveedor_producto SET estado=0 WHERE id_proveedor=? AND id_producto=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public List<String> listar() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT pr.nombre_proveedor, p.nombre_producto " +
                     "FROM proveedor_producto pp " +
                     "JOIN proveedores pr ON pr.id_proveedor = pp.id_proveedor " +
                     "JOIN productos p ON p.id_producto = pp.id_producto " +
                     "WHERE pp.estado=1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String fila = "Proveedor: " + rs.getString("nombre_proveedor") +
                              " | Producto: " + rs.getString("nombre_producto");
                lista.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}

