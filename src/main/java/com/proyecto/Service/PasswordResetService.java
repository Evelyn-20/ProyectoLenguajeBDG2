package com.proyecto.Service;

import com.proyecto.Domain.Cliente;
import com.proyecto.Dao.ClienteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PasswordResetService {

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private EmailService emailService;

    // Almacén temporal para tokens (en producción usar Redis o base de datos)
    private Map<String, PasswordResetToken> tokenStore = new HashMap<>();

    private static final int TOKEN_EXPIRY_HOURS = 24;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Genera y envía token de recuperación de contraseña
     * Cumple con RF-39: Olvidar Contraseña
     */
    public boolean solicitarRecuperacionContrasena(String email) {
        try {
            // RF-39 Precondición: El usuario debe estar registrado y activo
            if (!clienteDao.existeEmail(email)) {
                return false; // RF-39 Excepción 1: No hay usuario con ese correo
            }

            if (!clienteDao.verificarUsuarioActivo(email)) {
                return false; // Usuario inactivo (no está en requerimientos pero es buena práctica)
            }

            // Generar token único
            String token = generarToken();
            
            // Crear objeto de token con expiración
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setEmail(email);
            resetToken.setToken(token);
            resetToken.setExpiracion(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));

            // Guardar token (en memoria por ahora)
            tokenStore.put(token, resetToken);

            // Limpiar tokens expirados
            limpiarTokensExpirados();

            // RF-39 Paso 3: El sistema valida envía un correo al email del usuario
            return emailService.enviarEmailRecuperacion(email, token);

        } catch (Exception e) {
            System.err.println("Error al solicitar recuperación de contraseña: " + e.getMessage());
            throw new RuntimeException("Error al procesar solicitud"); // Para RF-39 Excepción 2
        }
    }

    /**
     * Valida el token de recuperación
     */
    public boolean validarToken(String token) {
        PasswordResetToken resetToken = tokenStore.get(token);
        
        if (resetToken == null) {
            return false; // Token no existe
        }

        if (resetToken.getExpiracion().isBefore(LocalDateTime.now())) {
            tokenStore.remove(token); // Remover token expirado
            return false; // Token expirado
        }

        return true;
    }

    /**
     * Restablece la contraseña usando el token
     * Cumple con RF-39 Paso 6: El sistema actualiza la información del usuario
     */
    @Transactional
    public boolean restablecerContrasena(String token, String nuevaContrasena) {
        try {
            // Validar token
            if (!validarToken(token)) {
                return false;
            }

            PasswordResetToken resetToken = tokenStore.get(token);
            String email = resetToken.getEmail();

            // Obtener cliente por email
            Cliente cliente = clienteDao.obtenerClientePorEmail(email);
            if (cliente == null) {
                return false;
            }

            // RF-39 Paso 6: El sistema actualiza la información del usuario
            clienteDao.actualizarCliente(cliente, nuevaContrasena);

            // Remover token usado
            tokenStore.remove(token);

            return true;

        } catch (Exception e) {
            System.err.println("Error al restablecer contraseña: " + e.getMessage());
            throw new RuntimeException("Error al actualizar contraseña"); // Para RF-39 Excepción 2
        }
    }

    /**
     * Obtiene email asociado al token
     */
    public String obtenerEmailPorToken(String token) {
        PasswordResetToken resetToken = tokenStore.get(token);
        return resetToken != null ? resetToken.getEmail() : null;
    }

    /**
     * Genera token aleatorio
     */
    private String generarToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < 32; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        return sb.toString();
    }

    /**
     * Limpia tokens expirados del almacén
     */
    private void limpiarTokensExpirados() {
        LocalDateTime ahora = LocalDateTime.now();
        tokenStore.entrySet().removeIf(entry -> entry.getValue().getExpiracion().isBefore(ahora));
    }

    /**
     * Clase interna para manejar tokens de recuperación
     */
    private static class PasswordResetToken {
        private String email;
        private String token;
        private LocalDateTime expiracion;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public LocalDateTime getExpiracion() { return expiracion; }
        public void setExpiracion(LocalDateTime expiracion) { this.expiracion = expiracion; }
    }
}