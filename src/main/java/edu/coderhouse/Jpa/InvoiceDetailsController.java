package edu.coderhouse.Jpa;

import edu.coderhouse.Jpa.ErrorResponse;
import edu.coderhouse.Jpa.InvoiceDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getInvoiceDetailsById(@PathVariable("id") int id) {
        Optional<InvoiceDetails> invoiceDetails = invoiceDetailsService.getInvoiceDetailsById(id);

        // Si se encuentra el detalle de la factura, retorna el OK con el detalle
        if (invoiceDetails.isPresent()) {
            return ResponseEntity.ok(invoiceDetails.get());
        } else {
            // Si no se encuentra, retorna un error con el mensaje adecuado
            ErrorResponse errorResponse = new ErrorResponse("Detalle de factura no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> guardarInvoiceDetails(@RequestBody InvoiceDetails invoiceDetails) {
        try {
            // Guardar el detalle de la factura (validación y persistencia)
            InvoiceDetails invoiceDetailsGuardado = invoiceDetailsService.crearInvoiceDetails(invoiceDetails);

            // Responder con el detalle de la factura creado
            return ResponseEntity.created(URI.create("/invoice_details/" + invoiceDetailsGuardado.getInvoiceDetailId()))
                    .body(invoiceDetailsGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}