package com.proyecto.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía email de recuperación de contraseña con logs detallados
     */
    public boolean enviarEmailRecuperacion(String email, String token) {
        try {
            logger.info("=== INICIANDO ENVÍO DE EMAIL ===");
            logger.info("Email destino: {}", email);
            logger.info("Token generado: {}", token);
            logger.info("Base URL: {}", baseUrl);

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            mensaje.setSubject("Nous - Recuperación de Contraseña");
            mensaje.setText(construirMensajeRecuperacion(token));
            mensaje.setFrom("noreply@nous.com");

            logger.info("Mensaje configurado:");
            logger.info("- Para: {}", mensaje.getTo()[0]);
            logger.info("- Asunto: {}", mensaje.getSubject());
            logger.info("- De: {}", mensaje.getFrom());

            // Intentar enviar el email
            mailSender.send(mensaje);
            
            logger.info("✅ Email enviado exitosamente a: {}", email);
            return true;

        } catch (Exception e) {
            logger.error("❌ ERROR al enviar email de recuperación:");
            logger.error("Email: {}", email);
            logger.error("Error: {}", e.getMessage());
            logger.error("Tipo de error: {}", e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Construye el mensaje de recuperación con el enlace
     */
    private String construirMensajeRecuperacion(String token) {
        String enlaceRecuperacion = baseUrl + "/reset-password/restablecer?token=" + token;
        
        logger.info("Enlace de recuperación generado: {}", enlaceRecuperacion);
        
        return "Estimado usuario,\n\n" +
               "Hemos recibido una solicitud para restablecer tu contraseña en Nous.\n\n" +
               "Para continuar con el proceso, haz clic en el siguiente enlace:\n" +
               enlaceRecuperacion + "\n\n" +
               "Este enlace expirará en 24 horas por motivos de seguridad.\n\n" +
               "Si no solicitaste este restablecimiento, puedes ignorar este mensaje.\n\n" +
               "Saludos,\n" +
               "Equipo de Nous";
    }
}