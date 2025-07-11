package com.proyecto.Controller;

import com.proyecto.Domain.Proveedor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProveedorController {
    
    @GetMapping("/proveedor/listado")
    public String verListado(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "usuario/listadoProveedores";
    }
    
    @GetMapping("/proveedor/agregar")
    public String agregar(Model model) {
        return "usuario/AgregarProveedor";
    }
}