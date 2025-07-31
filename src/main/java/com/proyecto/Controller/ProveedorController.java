package com.proyecto.Controller;

import com.proyecto.Domain.Proveedor;
import com.proyecto.Service.ProveedorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping("/listado")
    public String verListado(Model model,
            @RequestParam(required = false) String busqueda) {
        List<Proveedor> proveedores;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            proveedores = proveedorService.buscarProveedores(busqueda);
        } else {
            proveedores = proveedorService.obtenerTodosLosProveedores();
        }

        model.addAttribute("proveedores", proveedores);
        model.addAttribute("busqueda", busqueda);
        return "usuario/listadoProveedores";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);
            if (proveedor != null) {
                proveedorService.habilitarProveedor(proveedor.getCedula());
                redirectAttributes.addFlashAttribute("mensaje", "Proveedor activado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Proveedor no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al activar proveedor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/proveedor/listado";
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);
            if (proveedor != null) {
                proveedorService.deshabilitarProveedor(proveedor.getCedula());
                redirectAttributes.addFlashAttribute("mensaje", "Proveedor desactivado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Proveedor no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al desactivar proveedor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/proveedor/listado";
    }

    @GetMapping("/agregar")
    public String agregar(Model model) {
        Proveedor proveedor = new Proveedor();
        proveedor.setEstado("Activo"); // Valor por defecto
        model.addAttribute("proveedor", proveedor);
        return "usuario/AgregarProveedor";
    }

    @PostMapping("/guardar")
    public String guardarProveedor(@ModelAttribute Proveedor proveedor,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar y establecer estado si es null
            if (proveedor.getEstado() == null || proveedor.getEstado().isEmpty()) {
                proveedor.setEstado("Activo");
            }

            if (proveedor.getIdProveedor() == null) {
                proveedorService.registrarProveedor(proveedor);
                redirectAttributes.addFlashAttribute("mensaje", "Proveedor registrado exitosamente");
            } else {
                proveedorService.actualizarProveedor(proveedor);
                redirectAttributes.addFlashAttribute("mensaje", "Proveedor actualizado exitosamente");
            }
            
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/proveedor/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar proveedor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/proveedor/agregar";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);
        if (proveedor == null) {
            model.addAttribute("mensaje", "Proveedor no encontrado");
            return "redirect:/proveedor/listado";
        }
        model.addAttribute("proveedor", proveedor);
        return "usuario/ModificarProveedor";
    }
    
    @PostMapping("/modificar")
    public String actualizarCliente(@ModelAttribute Proveedor proveedor,
            RedirectAttributes redirectAttributes) {
        try {
            proveedorService.actualizarProveedor(proveedor);
            redirectAttributes.addFlashAttribute("mensaje", "Información actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
            return "redirect:/proveedor/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar información: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "usuario/modificarProveedor";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return desactivar(id, redirectAttributes);
    }

    @GetMapping("/buscar")
    public String buscarProveedores(@RequestParam String busqueda, Model model) {
        List<Proveedor> proveedores = proveedorService.buscarProveedores(busqueda);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("busqueda", busqueda);
        return "usuario/listadoProveedores";
    }
}