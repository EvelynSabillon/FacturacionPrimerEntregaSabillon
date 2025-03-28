package edu.coderhouse.Jpa;

import edu.coderhouse.Jpa.Invoice;
import edu.coderhouse.Jpa.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getInvoiceById(@PathVariable("id") int id) {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);

        if (invoice.isPresent()) {
            return ResponseEntity.ok(invoice.get());
        } else {
            // Aquí se crea el ErrorResponse y se retorna la respuesta con status NOT_FOUND
            ErrorResponse errorResponse = new ErrorResponse("Factura no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> guardarInvoice(@RequestBody Invoice invoice) {
        try {
            Invoice invoiceGuardada = invoiceService.crearInvoice(invoice);
            return ResponseEntity.created(URI.create("/invoice/" + invoiceGuardada.getId())).body(invoiceGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}