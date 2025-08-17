import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {

    public List<String> listarHistorial() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha_venta, c.nombre_cliente, p.nombre_producto, d.cantidad, d.subtotal " +
                     "FROM ventas v " +
                     "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "JOIN detalle_ventas d ON v.id_venta = d.id_venta " +
                     "JOIN productos p ON d.id_producto = p.id_producto";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String fila = "Venta #" + rs.getInt("id_venta") +
                              " | Cliente: " + rs.getString("nombre_cliente") +
                              " | Producto: " + rs.getString("nombre_producto") +
                              " | Cantidad: " + rs.getInt("cantidad") +
                              " | Subtotal: " + rs.getDouble("subtotal") +
                              " | Fecha: " + rs.getDate("fecha_venta");
                lista.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
