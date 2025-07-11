package com.proyecto.Controller;

import com.proyecto.Domain.Categoria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoriaController {
    
    @GetMapping("/categoria/listado")
    public String verListado(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categoria/listado";
    }
    
    @GetMapping("/categoria/agregar")
    public String agregar(Model model) {
        return "categoria/AgregarCategoria";
    }
}