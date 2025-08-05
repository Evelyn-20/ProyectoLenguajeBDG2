package com.proyecto.Controller;

import com.proyecto.Domain.Cliente;
import com.proyecto.Service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InicioController {

    private final ClienteService clienteService;

    public InicioController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        Cliente cliente = new Cliente();
        cliente.setEstado("Activo"); // Valor por defecto
        model.addAttribute("cliente", cliente);
        return "registrarse";
    }

    @PostMapping("/registro/guardar")
    public String guardarRegistro(@ModelAttribute Cliente cliente,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar y establecer estado si es null
            if (cliente.getEstado() == null || cliente.getEstado().isEmpty()) {
                cliente.setEstado("Activo");
            }

            clienteService.registrarCliente(cliente);
            redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso. Por favor inicie sesión.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/registro";
        }
    }

    @GetMapping("/recuperar-contrasena")
    public String mostrarRecuperarContrasena() {
        return "olvidarContra";
    }
}
