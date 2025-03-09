package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable("id") int id) {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Invoice> guardarInvoice(@RequestBody Invoice invoice) {
        Invoice invoiceGuardada = invoiceService.crearInvoice(invoice);
        return ResponseEntity.created(URI.create("/invoice/" + invoiceGuardada.getId())).body(invoiceGuardada);
    }
}