package com.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/login",
                        "/webjars/**",
                        "/img/**",
                        "/css/**",
                        "/js/**",
                        "/registro/**",
                        "/recuperar-contrasena",
                        "/mujer",
                        "/hombre",
                        "/zapatos",
                        "/accesorios",
                        "/producto/**",
                        "/cliente/listado",
                        "/cliente/agregar",
                        "/cliente/guardar",
                        "/cliente/modificar",
                        "/cliente/modificar/**",
                        "/cliente/activar/**",
                        "/cliente/desactivar/**",
                        "/proveedor/listado",
                        "/proveedor/agregar",
                        "/proveedor/guardar",
                        "/proveedor/modificar",
                        "/proveedor/modificar/**",
                        "/proveedor/activar/**",
                        "/proveedor/desactivar/**"
                ).permitAll()
                .requestMatchers(
                        "/cliente/perfil",
                        "/cliente/actualizar"
                ).authenticated() // Rutas específicas para clientes autenticados
                .requestMatchers(
                        "/cliente/**",
                        "/inventario/**"
                ).hasRole("ADMIN") // Rutas solo para admin
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true) // Cambiado a página principal
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
