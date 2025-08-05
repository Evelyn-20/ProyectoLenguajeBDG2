package com.proyecto.Controller;

import com.proyecto.Domain.Cliente;
import com.proyecto.Service.ClienteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/listado")
    public String verListado(Model model,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado) {

        List<Cliente> clientes;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            clientes = clienteService.buscarTodosClientes(busqueda);
        } else if (estado != null && !estado.isEmpty()) {
            if ("Activo".equals(estado)) {
                clientes = clienteService.obtenerClientesActivos();
            } else {
                clientes = clienteService.obtenerClientesInactivos();
            }
        } else {
            clientes = clienteService.obtenerTodosLosClientes();
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estado", estado);
        return "usuario/listadoClientes";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.obtenerClientePorId(id);
            if (cliente != null) {
                clienteService.activarCliente(cliente.getCedula());
                redirectAttributes.addFlashAttribute("mensaje", "Cliente activado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al activar cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cliente/listado";
    }

    // Nuevo método para desactivar clientes
    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.obtenerClientePorId(id);
            if (cliente != null) {
                clienteService.deshabilitarCliente(cliente.getCedula());
                redirectAttributes.addFlashAttribute("mensaje", "Cliente desactivado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al desactivar cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cliente/listado";
    }

    @GetMapping("/agregar")
    public String agregar(Model model) {
        Cliente cliente = new Cliente();
        cliente.setEstado("Activo"); // Valor por defecto
        model.addAttribute("cliente", cliente);
        return "usuario/AgregarCliente";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar y establecer estado si es null
            if (cliente.getEstado() == null || cliente.getEstado().isEmpty()) {
                cliente.setEstado("Activo");
            }

            clienteService.registrarCliente(cliente);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente registrado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/cliente/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/cliente/agregar";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerClientePorId(id);
        if (cliente == null) {
            model.addAttribute("mensaje", "Cliente no encontrado");
            return "redirect:/cliente/listado";
        }
        model.addAttribute("cliente", cliente);
        return "usuario/modificarCliente";
    }

    @PostMapping("/modificar")
    public String modificarCliente(@ModelAttribute Cliente cliente,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.modificarCliente(cliente);
            redirectAttributes.addFlashAttribute("mensaje", "Información actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/cliente/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar información: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/cliente/modificar/" + cliente.getIdCliente();
        }
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model) {
        // Obtener autenticación del contexto de seguridad
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Obtener username (cedula) del usuario autenticado
        String cedula = authentication.getName();

        // Buscar cliente
        Cliente cliente = clienteService.obtenerClientePorCedula(cedula);
        if (cliente == null) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", cliente);
        return "cliente/actualizarInformacionCliente";
    }

    @PostMapping("/actualizar")
    public String actualizarCliente(
            @ModelAttribute Cliente cliente,
            @RequestParam(required = false) String contra,
            @RequestParam(required = false) String verificar_contra,
            RedirectAttributes redirectAttributes) {

        try {
            // Validar contraseñas si se proporcionaron
            if (contra != null && !contra.isEmpty()) {
                if (!contra.equals(verificar_contra)) {
                    redirectAttributes.addFlashAttribute("mensaje", "Las contraseñas no coinciden");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/cliente/perfil";
                }
            }

            clienteService.actualizarCliente(cliente, contra);
            redirectAttributes.addFlashAttribute("mensaje", "Información actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar información: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }

        return "redirect:/cliente/perfil";
    }

    // Mantener este método por compatibilidad, pero redirigir a desactivar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return desactivar(id, redirectAttributes);
    }

    @GetMapping("/buscar")
    public String buscarClientes(@RequestParam String busqueda, Model model) {
        // Usar el método que busca en TODOS los clientes
        List<Cliente> clientes = clienteService.buscarTodosClientes(busqueda);
        model.addAttribute("clientes", clientes);
        model.addAttribute("busqueda", busqueda);
        return "usuario/listadoClientes";
    }
}
