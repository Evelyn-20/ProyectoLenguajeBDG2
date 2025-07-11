package com.proyecto.Controller;

import com.proyecto.Domain.Cliente;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ClienteController {
    
    @GetMapping("/cliente/listado")
    public String verListado(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "usuario/listadoClientes";
    }
    
    @GetMapping("/cliente/agregar")
    public String agregar(Model model) {
        return "usuario/AgregarCliente";
    }
}