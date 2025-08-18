package com.proyecto.Controller;

import com.proyecto.Domain.Asociacion;
import com.proyecto.Domain.Producto;
import com.proyecto.Domain.Proveedor;
import com.proyecto.Service.AsociacionService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asociacion")
public class AsociacionController {

    @Autowired
    private AsociacionService asociacionService;

    @GetMapping("/listado")
    public String verListado(Model model,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado) {

        List<Asociacion> asociaciones;

        try {
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                asociaciones = asociacionService.buscarTodasAsociaciones(busqueda);
            } else if (estado != null && !estado.isEmpty()) {
                if ("Activo".equals(estado)) {
                    asociaciones = asociacionService.obtenerAsociacionesActivas();
                } else {
                    asociaciones = asociacionService.obtenerAsociacionesInactivas();
                }
            } else {
                asociaciones = asociacionService.obtenerTodasLasAsociaciones();
            }

            model.addAttribute("asociaciones", asociaciones);
            model.addAttribute("busqueda", busqueda);
            model.addAttribute("estado", estado);
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error al cargar asociaciones: " + e.getMessage());
            model.addAttribute("tipoMensaje", "error");
            model.addAttribute("asociaciones", List.of());
        }

        return "asociacion/listado";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asociacionService.habilitarAsociacion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Asociación activada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al activar asociación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/asociacion/listado";
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asociacionService.deshabilitarAsociacion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Asociación desactivada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al desactivar asociación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/asociacion/listado";
    }

    @GetMapping("/agregar")
    public String agregar(Model model) {
        try {
            Asociacion asociacion = new Asociacion();
            asociacion.setEstado("Activo"); // Valor por defecto

            List<Producto> productos = asociacionService.obtenerProductosActivos();
            List<Proveedor> proveedores = asociacionService.obtenerProveedoresActivos();

            model.addAttribute("asociacion", asociacion);
            model.addAttribute("productos", productos);
            model.addAttribute("proveedores", proveedores);
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error al cargar formulario: " + e.getMessage());
            model.addAttribute("tipoMensaje", "error");
        }
        return "asociacion/agregarAsociacion";
    }

    @PostMapping("/guardar")
    public String guardarAsociacion(@ModelAttribute Asociacion asociacion,
            RedirectAttributes redirectAttributes) {
        try {
            // Validar y establecer estado si es null
            if (asociacion.getEstado() == null || asociacion.getEstado().isEmpty()) {
                asociacion.setEstado("Activo");
            }

            if (asociacion.getProductoProveedorId() == null) {
                // Verificar si ya existe la asociación
                if (asociacionService.existeAsociacion(asociacion.getProductoId(), asociacion.getProveedorId())) {
                    redirectAttributes.addFlashAttribute("mensaje", "Ya existe una asociación activa entre este producto y proveedor");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                    return "redirect:/asociacion/agregar";
                }

                asociacionService.registrarAsociacion(asociacion);
                redirectAttributes.addFlashAttribute("mensaje", "Asociación registrada exitosamente");
            } else {
                asociacionService.actualizarAsociacion(asociacion);
                redirectAttributes.addFlashAttribute("mensaje", "Asociación actualizada exitosamente");
            }

            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/asociacion/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar asociación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/asociacion/agregar";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, Model model) {
        try {
            Asociacion asociacion = asociacionService.obtenerAsociacionPorId(id);
            if (asociacion == null) {
                model.addAttribute("mensaje", "Asociación no encontrada");
                model.addAttribute("tipoMensaje", "error");
                return "redirect:/asociacion/listado";
            }

            List<Producto> productos = asociacionService.obtenerProductosActivos();
            List<Proveedor> proveedores = asociacionService.obtenerProveedoresActivos();

            model.addAttribute("asociacion", asociacion);
            model.addAttribute("productos", productos);
            model.addAttribute("proveedores", proveedores);
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error al cargar asociación: " + e.getMessage());
            model.addAttribute("tipoMensaje", "error");
            return "redirect:/asociacion/listado";
        }
        return "asociacion/modificarAsociacion";
    }

    @PostMapping("/modificar")
    public String actualizarAsociacion(@ModelAttribute Asociacion asociacion,
            RedirectAttributes redirectAttributes) {
        try {
            asociacionService.actualizarAsociacion(asociacion);
            redirectAttributes.addFlashAttribute("mensaje", "Asociación actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/asociacion/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar asociación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/asociacion/modificar/" + asociacion.getProductoProveedorId();
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return desactivar(id, redirectAttributes);
    }

    @GetMapping("/buscar")
    public String buscarAsociaciones(@RequestParam String busqueda, Model model) {
        try {
            List<Asociacion> asociaciones = asociacionService.buscarAsociaciones(busqueda);
            model.addAttribute("asociaciones", asociaciones);
            model.addAttribute("busqueda", busqueda);
            return "asociacion/listado";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error en la búsqueda: " + e.getMessage());
            model.addAttribute("tipoMensaje", "error");
            model.addAttribute("asociaciones", List.of());
            return "asociacion/listado";
        }
    }
}
