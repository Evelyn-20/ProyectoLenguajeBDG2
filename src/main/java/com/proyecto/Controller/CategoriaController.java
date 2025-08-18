package com.proyecto.Controller;

import com.proyecto.Domain.Categoria;
import com.proyecto.Service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/categoria/listado")
    public String verListado(Model model,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            @RequestParam(value = "estado", required = false) String estado) {

        List<Categoria> categorias;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            categorias = categoriaService.buscarPorNombre(busqueda.trim());
        } else if ("Activo".equals(estado)) {
            categorias = categoriaService.getCategorias();
        } else if ("Inactivo".equals(estado)) {
            categorias = categoriaService.getCategoriasInactivas();
        } else {
            categorias = categoriaService.getTodasLasCategorias();
        }

        model.addAttribute("categorias", categorias);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estado", estado);

        return "categoria/listado";
    }

    @GetMapping("/categoria/agregar")
    public String agregar(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categoria/AgregarCategoria";
    }

    @PostMapping("/categoria/guardar")
    public String guardar(Categoria categoria, RedirectAttributes redirectAttributes) {
        try {
            // Logs de depuración
            System.out.println("=== DATOS CATEGORÍA A GUARDAR ===");
            System.out.println("ID: " + categoria.getCategoriaId());
            System.out.println("Código: " + categoria.getCodigoCategoria());
            System.out.println("Nombre: " + categoria.getNombreCategoria());
            System.out.println("Descripción: " + categoria.getDescripcionCategoria());
            System.out.println("Estado: " + categoria.getEstado());

            // Validaciones
            if (categoria.getCodigoCategoria() == null || categoria.getCodigoCategoria() <= 0) {
                redirectAttributes.addFlashAttribute("mensaje", "El código de categoría es obligatorio y debe ser positivo");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/categoria/agregar";
            }

            if (categoria.getNombreCategoria() == null || categoria.getNombreCategoria().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "El nombre de categoría es obligatorio");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/categoria/agregar";
            }

            // Asegurar estado activo
            categoria.setEstado("Activo");

            // Guardar categoría
            categoriaService.registrarCategoria(categoria);

            redirectAttributes.addFlashAttribute("mensaje", "Categoría registrada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (DataIntegrityViolationException e) {
            String mensajeError = "Error al registrar la categoría: ";
            if (e.getMessage().contains("CODIGO_CATEGORIA")) {
                mensajeError += "El código de categoría ya existe";
            } else if (e.getMessage().contains("NOMBRE_CATEGORIA")) {
                mensajeError += "El nombre de categoría ya existe";
            } else {
                mensajeError += e.getMessage();
            }
            redirectAttributes.addFlashAttribute("mensaje", mensajeError);
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/categoria/agregar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error inesperado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/categoria/agregar";
        }
        return "redirect:/categoria/listado";
    }

    @GetMapping("/categoria/modificar/{id}")
    public String modificar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaService.getCategoriaPorId(id);
            if (categoria != null) {
                model.addAttribute("categoria", categoria);
                return "categoria/modificar";
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Categoría no encontrada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/categoria/listado";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar la categoría: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/categoria/listado";
        }
    }

    @PostMapping("/categoria/modificar")
    public String actualizar(Categoria categoria, RedirectAttributes redirectAttributes) {
        try {
            // Logs de depuración
            System.out.println("=== DATOS CATEGORÍA A ACTUALIZAR ===");
            System.out.println("ID: " + categoria.getCategoriaId());
            System.out.println("Código: " + categoria.getCodigoCategoria());
            System.out.println("Nombre: " + categoria.getNombreCategoria());
            System.out.println("Descripción: " + categoria.getDescripcionCategoria());
            System.out.println("Estado: " + categoria.getEstado());

            // Validaciones básicas
            if (categoria.getCategoriaId() == null) {
                redirectAttributes.addFlashAttribute("mensaje", "ID de categoría requerido");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/categoria/listado";
            }

            if (categoria.getCodigoCategoria() == null || categoria.getCodigoCategoria() <= 0) {
                redirectAttributes.addFlashAttribute("mensaje", "El código de categoría es obligatorio y debe ser positivo");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/categoria/modificar/" + categoria.getCategoriaId();
            }

            if (categoria.getNombreCategoria() == null || categoria.getNombreCategoria().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "El nombre de categoría es obligatorio");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/categoria/modificar/" + categoria.getCategoriaId();
            }

            if (categoria.getDescripcionCategoria() == null || categoria.getDescripcionCategoria().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "La descripción es obligatoria");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/categoria/modificar/" + categoria.getCategoriaId();
            }

            // Asegurar que el estado sea válido
            if (categoria.getEstado() == null
                    || (!categoria.getEstado().equals("Activo") && !categoria.getEstado().equals("Inactivo"))) {
                categoria.setEstado("Activo"); // Valor por defecto
            }

            // Actualizar categoría
            categoriaService.actualizarCategoria(categoria);

            redirectAttributes.addFlashAttribute("mensaje", "Categoría actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (DataIntegrityViolationException e) {
            String mensajeError = "Error al actualizar la categoría: ";
            if (e.getMessage().contains("CODIGO_CATEGORIA")) {
                mensajeError += "El código de categoría ya existe";
            } else if (e.getMessage().contains("NOMBRE_CATEGORIA")) {
                mensajeError += "El nombre de categoría ya existe";
            } else {
                mensajeError += e.getMessage();
            }
            redirectAttributes.addFlashAttribute("mensaje", mensajeError);
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/categoria/modificar/" + categoria.getCategoriaId();
        } catch (Exception e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            e.printStackTrace();

            String mensajeError = e.getMessage();
            if (mensajeError.contains("Código o nombre de categoría duplicado")) {
                mensajeError = "Ya existe una categoría con ese código o nombre";
            } else if (mensajeError.contains("Categoría no encontrada")) {
                mensajeError = "La categoría que intenta actualizar no existe";
            } else if (mensajeError.contains("El código de categoría ya existe")) {
                mensajeError = "El código de categoría ya está en uso";
            } else if (mensajeError.contains("El nombre de categoría ya existe")) {
                mensajeError = "El nombre de categoría ya está en uso";
            } else {
                mensajeError = "Error inesperado: " + mensajeError;
            }

            redirectAttributes.addFlashAttribute("mensaje", mensajeError);
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/categoria/modificar/" + categoria.getCategoriaId();
        }
        return "redirect:/categoria/listado";
    }

    @GetMapping("/categoria/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.deshabilitarCategoria(id);
            redirectAttributes.addFlashAttribute("mensaje", "Categoría deshabilitada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al deshabilitar la categoría: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/categoria/listado";
    }

    @GetMapping("/categoria/habilitar/{id}")
    public String habilitar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.habilitarCategoria(id);
            redirectAttributes.addFlashAttribute("mensaje", "Categoría habilitada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al habilitar la categoría: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/categoria/listado";
    }
}
