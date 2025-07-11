package com.proyecto.Controller;

import com.proyecto.Domain.Asociacion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AsociacionController {
    
    @GetMapping("/asociacion/listado")
    public String verListado(Model model) {
        model.addAttribute("asociacion", new Asociacion());
        return "asociacion/listado";
    }
    
    @GetMapping("/asociacion/agregar")
    public String agregar(Model model) {
        return "asociacion/agregarAsociacion";
    }
}