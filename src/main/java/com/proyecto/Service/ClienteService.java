package com.proyecto.Service;

import com.proyecto.Domain.Cliente;
import com.proyecto.Dao.ClienteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteDao clienteRepository;

    @Transactional
    public void registrarCliente(Cliente cliente) {
        clienteRepository.registrarCliente(cliente);
    }

    @Transactional
    public void actualizarCliente(Cliente cliente, String nuevaContrasena) {
        clienteRepository.actualizarCliente(cliente, nuevaContrasena);
    }
    
    @Transactional
    public void modificarCliente(Cliente cliente) {
        clienteRepository.modificarCliente(cliente);
    }

    @Transactional
    public void deshabilitarCliente(String cedula) {
        clienteRepository.deshabilitarCliente(cedula);
    }

    @Transactional
    public void activarCliente(String cedula) {
        clienteRepository.activarCliente(cedula);
    }

    public List<Cliente> obtenerClientesActivos() {
        return clienteRepository.obtenerClientesActivos();
    }

    public List<Cliente> obtenerClientesInactivos() {
        return clienteRepository.obtenerClientesInactivos();
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.obtenerTodosLosClientes();
    }

    public List<Cliente> buscarClientes(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        return clienteRepository.buscarClientes(busqueda);
    }

    public List<Cliente> buscarTodosClientes(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        return clienteRepository.buscarTodosClientes(busqueda);
    }

    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.obtenerClientePorId(id);
    }

    public Cliente obtenerClientePorCedula(String cedula) {
        return clienteRepository.obtenerClientePorCedula(cedula);
    }

    public boolean existeEmail(String email) {
        return clienteRepository.existeEmail(email);
    }

    public boolean verificarUsuarioActivo(String email) {
        return clienteRepository.verificarUsuarioActivo(email);
    }
}