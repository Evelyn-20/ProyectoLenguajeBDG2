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

    // Método principal para obtener TODOS los clientes (activos e inactivos)
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.obtenerTodosLosClientes();
    }

    // Método principal para buscar en TODOS los clientes (activos e inactivos)
    public List<Cliente> buscarClientes(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        return clienteRepository.buscarTodosClientes(busqueda);
    }

    // Método alternativo que hace lo mismo que buscarClientes para mayor claridad
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
}
