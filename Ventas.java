import java.util.Date;

public class Venta {
    private int idVenta;
    private int idCliente;
    private Date fecha;

    public Venta() {}

    public Venta(int idVenta, int idCliente, Date fecha) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.fecha = fecha;
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
