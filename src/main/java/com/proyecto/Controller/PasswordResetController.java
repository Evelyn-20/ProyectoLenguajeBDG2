package com.proyecto.Controller;

import com.proyecto.Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reset-password") // Mantener esta ruta como la tienes
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Muestra el formulario para solicitar recuperación
     */
    @GetMapping
    public String mostrarFormularioRecuperacion() {
        return "olvidarContra";
    }

    /**
     * Procesa la solicitud de recuperación de contraseña
     */
    @PostMapping("/solicitar")
    public String solicitarRecuperacion(@RequestParam String email, 
                                      RedirectAttributes redirectAttributes) {
        try {
            boolean exito = passwordResetService.solicitarRecuperacionContrasena(email);
            
            if (exito) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Se ha enviado un correo electrónico con las instrucciones para recuperar su contraseña.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "No hay ningún usuario con ese correo registrado.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al procesar la solicitud. Intente nuevamente.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        // CORREGIDO: Usar la ruta correcta del controlador
        return "redirect:/reset-password";
    }

    /**
     * Muestra el formulario para restablecer contraseña con token
     */
    @GetMapping("/restablecer")
    public String mostrarFormularioRestablecimiento(@RequestParam String token, 
                                                  Model model,
                                                  RedirectAttributes redirectAttributes) {
        
        if (!passwordResetService.validarToken(token)) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "El enlace de recuperación es inválido o ha expirado.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        String email = passwordResetService.obtenerEmailPorToken(token);
        
        model.addAttribute("token", token);
        model.addAttribute("email", email);
        return "recuperarContra";
    }

    /**
     * Procesa el restablecimiento de contraseña
     */
    @PostMapping("/restablecer")
    public String restablecerContrasena(@RequestParam String token,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        
        try {
            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("mensaje", "Las contraseñas no coinciden.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                // CORREGIDO: Usar la ruta correcta del controlador
                return "redirect:/reset-password/restablecer?token=" + token;
            }

            // Validar longitud mínima
            if (password.length() < 8) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "La contraseña debe tener al menos 8 caracteres.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/reset-password/restablecer?token=" + token;
            }

            // Validaciones adicionales de contraseña
            if (!esContrasenaValida(password)) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "La contraseña debe contener al menos una mayúscula, una minúscula y un número.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/reset-password/restablecer?token=" + token;
            }

            String contrasenaEncriptada = passwordEncoder.encode(password);
            boolean exito = passwordResetService.restablecerContrasena(token, contrasenaEncriptada);

            if (exito) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "El usuario cambió su contraseña exitosamente.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Error al actualizar la contraseña. El token puede haber expirado.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/login";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al actualizar la contraseña. Intente nuevamente.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/reset-password/restablecer?token=" + token;
        }
    }
    
    /**
     * Validación mejorada de contraseña
     */
    private boolean esContrasenaValida(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
    }
}