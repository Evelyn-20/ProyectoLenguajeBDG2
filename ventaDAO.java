import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public void registrar(int idCliente, int idProducto, int cantidad) {
        String sqlVenta = "INSERT INTO ventas(id_cliente, fecha_venta, estado) VALUES(?, SYSDATE, 'ACTIVA')";
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

    public String consultar(int idVenta) {
        String sql = "SELECT v.id_venta, v.fecha_venta, v.estado, c.nombre_cliente, p.nombre_producto, d.cantidad, d.subtotal " +
                     "FROM ventas v " +
                     "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "JOIN detalle_ventas d ON v.id_venta = d.id_venta " +
                     "JOIN productos p ON d.id_producto = p.id_producto " +
                     "WHERE v.id_venta=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Venta #" + rs.getInt("id_venta") +
                       " | Cliente: " + rs.getString("nombre_cliente") +
                       " | Producto: " + rs.getString("nombre_producto") +
                       " | Cantidad: " + rs.getInt("cantidad") +
                       " | Subtotal: " + rs.getDouble("subtotal") +
                       " | Estado: " + rs.getString("estado") +
                       " | Fecha: " + rs.getDate("fecha_venta");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cambiarEstado(int idVenta, String nuevoEstado) {
        String sql = "UPDATE ventas SET estado=? WHERE id_venta=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idVenta);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> listar() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha_venta, v.estado, c.nombre_cliente " +
                     "FROM ventas v " +
                     "JOIN clientes c ON v.id_cliente = c.id_cliente";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String fila = "Venta #" + rs.getInt("id_venta") +
                              " | Cliente: " + rs.getString("nombre_cliente") +
                              " | Estado: " + rs.getString("estado") +
                              " | Fecha: " + rs.getDate("fecha_venta");
                lista.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<String> historialCompras(int idCliente) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha_venta, p.nombre_producto, d.cantidad, d.subtotal " +
                     "FROM ventas v " +
                     "JOIN detalle_ventas d ON v.id_venta = d.id_venta " +
                     "JOIN productos p ON d.id_producto = p.id_producto " +
                     "WHERE v.id_cliente=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fila = "Venta #" + rs.getInt("id_venta") +
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
    }
}
