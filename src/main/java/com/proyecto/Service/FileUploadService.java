package com.proyecto.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${app.upload.dir:src/main/resources/static/img/}")
    private String uploadDir;
    
    public String guardarImagen(MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }
        
        // Validar tipo de archivo
        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("El archivo debe ser una imagen");
        }
        
        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generar nombre único para el archivo con prefijo "producto_"
        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = "producto_" + System.currentTimeMillis() + "_" + 
                              UUID.randomUUID().toString().substring(0, 8) + "." + extension;
        
        // Guardar archivo en static/img
        Path rutaArchivo = uploadPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
        
        // Retornar solo el nombre del archivo (se guardará así en BD)
        return nombreArchivo;
    }
    
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "jpg"; // Extensión por defecto
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
    }
    
    public void eliminarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.equals("default-image.jpg") || 
            !nombreImagen.startsWith("producto_")) {
            return; // No eliminar imágenes por defecto o que no sean subidas por usuarios
        }
        
        try {
            Path rutaArchivo = Paths.get(uploadDir + nombreImagen);
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            // Log del error, pero no fallar la operación
            System.err.println("Error al eliminar imagen: " + e.getMessage());
        }
    }
}