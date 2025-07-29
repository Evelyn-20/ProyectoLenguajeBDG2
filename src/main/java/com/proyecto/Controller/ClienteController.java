package com.proyecto.Controller;

import com.proyecto.Domain.Cliente;
import com.proyecto.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping("/listado")
    public String verListado(Model model, @RequestParam(required = false) String busqueda) {
        List<Cliente> clientes;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            clientes = clienteService.buscarClientes(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else {
            clientes = clienteService.obtenerTodosLosClientes();
        }
        model.addAttribute("clientes", clientes);
        return "usuario/listadoClientes";
    }
    
    @GetMapping("/agregar")
    public String agregar(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "usuario/AgregarCliente";
    }
    
    @PostMapping("/agregar")
    public String guardarCliente(@ModelAttribute Cliente cliente, 
                               RedirectAttributes redirectAttributes) {
        try {
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
        return "usuario/ModificarCliente";
    }
    
    @PostMapping("/modificar")
    public String actualizarCliente(@ModelAttribute Cliente cliente, 
                                  RedirectAttributes redirectAttributes) {
        try {
            clienteService.actualizarCliente(cliente);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/cliente/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/cliente/modificar/" + cliente.getIdCliente();
        }
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.obtenerClientePorId(id);
            if (cliente != null) {
                clienteService.deshabilitarCliente(cliente.getCedula());
                redirectAttributes.addFlashAttribute("mensaje", "Cliente deshabilitado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cliente/listado";
    }
    
    @GetMapping("/buscar")
    public String buscarClientes(@RequestParam String busqueda, Model model) {
        List<Cliente> clientes = clienteService.buscarClientes(busqueda);
        model.addAttribute("clientes", clientes);
        model.addAttribute("busqueda", busqueda);
        return "usuario/listadoClientes";
    }
}