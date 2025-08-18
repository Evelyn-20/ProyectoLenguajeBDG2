package com.proyecto.Service;

import com.proyecto.Dao.ClienteDao;
import com.proyecto.Domain.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Buscar admin primero
        try {
            String sql = "SELECT USERNAME, PASSWORD FROM ADMIN WHERE USERNAME = ?";
            List<Object[]> adminResult = jdbcTemplate.query(sql, 
                (rs, rowNum) -> new Object[]{rs.getString("USERNAME"), rs.getString("PASSWORD")}, 
                username);
            
            if (!adminResult.isEmpty()) {
                Object[] admin = adminResult.get(0);
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                
                return new User(
                    (String) admin[0], // username
                    (String) admin[1], // password en texto plano
                    true, // enabled
                    true, // accountNonExpired
                    true, // credentialsNonExpired
                    true, // accountNonLocked
                    authorities
                );
            }
        } catch (Exception e) {
            // Si no encuentra admin, continúa buscando cliente
        }

        // Buscar cliente por email o cédula
        Cliente cliente = null;
        
        // Primero intentar buscar por email
        cliente = clienteDao.obtenerClientePorEmail(username);
        
        // Si no se encuentra por email, buscar por cédula
        if (cliente == null) {
            cliente = clienteDao.obtenerClientePorCedula(username);
        }
        
        if (cliente != null) {
            // Verificar que el cliente esté activo
            if (!"Activo".equals(cliente.getEstado())) {
                throw new UsernameNotFoundException("Usuario inactivo: " + username);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            return new User(
                cliente.getEmail(), // usar email como username principal
                cliente.getContrasena(), // password en texto plano
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
            );
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}