package com.proyecto.Domain;

public class Asociacion {
    private Long productoProveedorId;
    private Long productoId;
    private Long proveedorId;
    private double precioCompra;
    private String estado;
    
    // Getters y Setters
    public Long getProductoProveedorId() {
        return productoProveedorId;
    }
    
    public void setProductoProveedorId(Long productoProveedorId) {
        this.productoProveedorId = productoProveedorId;
    }
    
    public Long getProductoId() {
        return productoId;
    }
    
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
    
    public Long getProveedorId() {
        return proveedorId;
    }
    
    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }
    
    public double getPrecioCompra() {
        return precioCompra;
    }
    
    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
