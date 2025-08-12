package com.proyecto.Domain;

public class Categoria {
    private Long categoriaId;
    private String codigoCategoria;
    private String nombreCategoria;
    private String descripcionCategoria;
    private String estado;
    private String imagenProducto;
    private String codigoUrl; // Para el mapeo de URLs dinámicas
    
    // Constructor por defecto
    public Categoria() {}
    
    // Constructor con parámetros principales
    public Categoria(String nombreCategoria, String descripcionCategoria) {
        this.nombreCategoria = nombreCategoria;
        this.descripcionCategoria = descripcionCategoria;
        this.estado = "Activo";
        this.imagenProducto = "default-image.jpg";
    }
    
    // Getters y Setters
    public Long getCategoriaId() {
        return categoriaId;
    }
    
    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
    
    public String getCodigoCategoria() {
        return codigoCategoria;
    }
    
    public void setCodigoCategoria(String codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }
    
    public String getNombreCategoria() {
        return nombreCategoria;
    }
    
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
    
    public String getDescripcionCategoria() {
        return descripcionCategoria;
    }
    
    public void setDescripcionCategoria(String descripcionCategoria) {
        this.descripcionCategoria = descripcionCategoria;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getImagenProducto() {
        return imagenProducto;
    }
    
    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
    
    public String getCodigoUrl() {
        return codigoUrl;
    }
    
    public void setCodigoUrl(String codigoUrl) {
        this.codigoUrl = codigoUrl;
    }
    
    // Método helper para obtener la ruta completa de la imagen
    public String getRutaImagenCompleta() {
        if (imagenProducto != null && !imagenProducto.isEmpty()) {
            return "/img/" + imagenProducto;
        }
        return "/img/default-image.jpg";
    }
    
    // Método helper para verificar si la categoría está activa
    public boolean isActiva() {
        return "Activo".equals(estado);
    }
}