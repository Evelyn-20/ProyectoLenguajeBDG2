package com.proyecto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class ClienteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall iniciarSesionCall;

    @PostConstruct
    void init() {
        iniciarSesionCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("iniciar_sesion_usuario");
    }

    public Map<String, Object> iniciarSesion(String email, String password) {
        return iniciarSesionCall.execute(Map.of(
                "p_email", email,
                "p_password", password
        ));
    }
}
