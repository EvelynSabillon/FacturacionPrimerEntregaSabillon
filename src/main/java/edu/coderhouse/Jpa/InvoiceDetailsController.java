package edu.coderhouse.Jpa;

import edu.coderhouse.Jpa.InvoiceDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/invoice_details")
public class InvoiceDetailsController {
    @Autowired
    private InvoiceDetailsService invoiceDetailsService;

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetails> getInvoiceDetailsById(@PathVariable("id") int id) {
        Optional<InvoiceDetails> invoiceDetails = invoiceDetailsService.getInvoiceDetailsById(id);
        return invoiceDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InvoiceDetails> guardarInvoiceDetails(@RequestBody InvoiceDetails invoiceDetails) {
        InvoiceDetails invoiceDetailsGuardada = invoiceDetailsService.crearInvoiceDetails(invoiceDetails);
        return ResponseEntity.created(URI.create("/invoice_details/" + invoiceDetailsGuardada.getInvoiceDetailId())).body(invoiceDetailsGuardada);
    }
}