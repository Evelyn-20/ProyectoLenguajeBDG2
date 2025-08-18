package com.proyecto.Domain;

public class Asociacion {
    private Long productoProveedorId;
    private Long productoId;
    private Long proveedorId;
    private Double precioCompra; // Cambiado de double a Double para permitir null
    private String estado;
    
    // Campos adicionales para mostrar información completa en las vistas
    private String nombreProducto;
    private Long codigoProducto; // Cambiado de String a Long para consistencia
    private String nombreProveedor;
    private String apellidosProveedor;
    
    // Constructor por defecto
    public Asociacion() {
    }
    
    // Constructor con parámetros básicos
    public Asociacion(Long productoId, Long proveedorId, Double precioCompra) {
        this.productoId = productoId;
        this.proveedorId = proveedorId;
        this.precioCompra = precioCompra;
        this.estado = "Activo";
    }
    
    // Getters y Setters básicos
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
    
    public Double getPrecioCompra() {
        return precioCompra;
    }
    
    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    // Getters y Setters para campos adicionales
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public Long getCodigoProducto() { // Cambiado a Long
        return codigoProducto;
    }
    
    public void setCodigoProducto(Long codigoProducto) { // Cambiado a Long
        this.codigoProducto = codigoProducto;
    }
    
    public String getNombreProveedor() {
        return nombreProveedor;
    }
    
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
    
    public String getApellidosProveedor() {
        return apellidosProveedor;
    }
    
    public void setApellidosProveedor(String apellidosProveedor) {
        this.apellidosProveedor = apellidosProveedor;
    }
    
    // Métodos de utilidad
    public String getNombreCompletoProveedor() {
        if (nombreProveedor != null && apellidosProveedor != null) {
            return nombreProveedor + " " + apellidosProveedor;
        }
        return nombreProveedor != null ? nombreProveedor : "";
    }
    
    public boolean isActivo() {
        return "Activo".equals(estado);
    }
    
    // Método para obtener código como String cuando sea necesario para mostrar
    public String getCodigoProductoString() {
        return codigoProducto != null ? codigoProducto.toString() : "";
    }
}