package com.proyecto.Controller;

import com.proyecto.Domain.Carrito;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CarritoController {
    
    @GetMapping("/ventas/listado")
    public String verListado(Model model) {
        model.addAttribute("ventas", new Carrito());
        return "carrito/listadoVentas";
    }

    @GetMapping("/historialCompras")
    public String verHistorialCompras(Model model) {
        model.addAttribute("historial", new Carrito());
        return "carrito/historialCompras";
    }
}