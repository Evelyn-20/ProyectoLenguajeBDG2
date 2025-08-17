import java.sql.*;

public class VentaDAO {

    public void registrarVenta(int idCliente, int idProducto, int cantidad) {
        String sqlVenta = "INSERT INTO ventas(id_cliente, fecha_venta) VALUES(?, SYSDATE)";
        String sqlDetalle = "INSERT INTO detalle_ventas(id_venta, id_producto, cantidad, subtotal) VALUES(?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection()) {
            con.setAutoCommit(false);

            PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, idCliente);
            psVenta.executeUpdate();

            ResultSet rs = psVenta.getGeneratedKeys();
            int idVenta = 0;
            if (rs.next()) idVenta = rs.getInt(1);

            double precio = 0;
            PreparedStatement psPrecio = con.prepareStatement("SELECT precio FROM productos WHERE id_producto=?");
            psPrecio.setInt(1, idProducto);
            ResultSet rsP = psPrecio.executeQuery();
            if (rsP.next()) precio = rsP.getDouble("precio");

            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
            psDetalle.setInt(1, idVenta);
            psDetalle.setInt(2, idProducto);
            psDetalle.setInt(3, cantidad);
            psDetalle.setDouble(4, precio * cantidad);
            psDetalle.executeUpdate();

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
