package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> getClienteById(int id) {
        return clienteRepository.findById(id);
    }
}
