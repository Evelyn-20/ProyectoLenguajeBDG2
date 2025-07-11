package com.proyecto.Controller;

import com.proyecto.Domain.Carrito;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class VentasController {
    
    @GetMapping("/venta/listado")
    public String verListado(Model model) {
        model.addAttribute("ventas", new Carrito());
        return "usuario/listadoProveedor";
    }
    
}