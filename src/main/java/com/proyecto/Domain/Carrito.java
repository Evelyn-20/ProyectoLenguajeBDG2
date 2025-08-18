package com.proyecto.Domain;

import java.time.LocalDateTime;
import java.util.List;

public class Carrito {
    private Long ventaId;
    private String codigoVenta;
    private Long clienteId;
    private LocalDateTime fechaVenta;
    private double subtotal;
    private double impuesto;
    private double descuento;
    private double total;
    private String medioPago;
    private String estado;
    
    private String cliente;

    private Long detalleId;
    private Long inventarioId;
    private int cantidad;
    private double precioUnitario;
    private double subtotalDetalle;
    private double descuentoItem;
    
    private String nombreProducto;
    private String imagenProducto;
    private String descripcionProducto;
    
    // Getters y Setters
    public Long getVentaId() {
        return ventaId;
    }
    
    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }
    
    public String getCodigoVenta() {
        return codigoVenta;
    }
    
    public void setCodigoVenta(String codigoVenta) {
        this.codigoVenta = codigoVenta;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    public double getImpuesto() {
        return impuesto;
    }
    
    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }
    
    public double getDescuento() {
        return descuento;
    }
    
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public String getMedioPago() {
        return medioPago;
    }
    
    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getDetalleId() {
        return detalleId;
    }
    
    public void setDetalleId(Long detalleId) {
        this.detalleId = detalleId;
    }
    
    public Long getInventarioId() {
        return inventarioId;
    }
    
    public void setInventarioId(Long inventarioId) {
        this.inventarioId = inventarioId;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public double getSubtotalDetalle() {
        return subtotalDetalle;
    }
    
    public void setSubtotalDetalle(double subtotalDetalle) {
        this.subtotalDetalle = subtotalDetalle;
    }
    
    public double getDescuentoItem() {
        return descuentoItem;
    }
    
    public void setDescuentoItem(double descuentoItem) {
        this.descuentoItem = descuentoItem;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public String getImagenProducto() {
        return imagenProducto;
    }
    
    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
    
    public String getDescripcionProducto() {
        return descripcionProducto;
    }
    
    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }
    
    public String getCliente() {
        return cliente;
    }
    
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}
