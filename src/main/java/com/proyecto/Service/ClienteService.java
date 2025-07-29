package com.proyecto.Service;

import com.proyecto.Domain.Cliente;
import com.proyecto.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Transactional
    public void registrarCliente(Cliente cliente) {
        clienteRepository.registrarCliente(cliente);
    }
    
    @Transactional
    public void actualizarCliente(Cliente cliente) {
        clienteRepository.actualizarCliente(cliente);
    }
    
    @Transactional
    public void deshabilitarCliente(String cedula) {
        clienteRepository.deshabilitarCliente(cedula);
    }
    
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.obtenerClientesActivos();
    }
    
    public List<Cliente> buscarClientes(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        return clienteRepository.buscarClientes(busqueda);
    }
    
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.obtenerClientePorId(id);
    }
    
    public Cliente obtenerClientePorCedula(String cedula) {
        return clienteRepository.obtenerClientePorCedula(cedula);
    }
}
