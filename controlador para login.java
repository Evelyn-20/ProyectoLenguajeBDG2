package com.proyecto.controller;

import com.proyecto.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String email, @RequestParam String password) {
        return clienteService.iniciarSesion(email, password);
    }
}
