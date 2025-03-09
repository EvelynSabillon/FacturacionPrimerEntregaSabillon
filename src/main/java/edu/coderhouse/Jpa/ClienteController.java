package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") int id) {
        Optional<Cliente> cliente = clienteService.getClienteById(id);
        return cliente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> guardarCliente(@RequestBody Cliente cliente) {
        Cliente clienteGuardado = clienteService.crearCliente(cliente);
        return ResponseEntity.created(URI.create("/client/" + clienteGuardado.getId())).body(clienteGuardado);
    }
}