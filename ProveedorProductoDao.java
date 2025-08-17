import java.sql.*;

public class ProveedorProductoDAO {

    public void asociar(int idProveedor, int idProducto) {
        String sql = "INSERT INTO proveedor_producto(id_proveedor, id_producto) VALUES(?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
