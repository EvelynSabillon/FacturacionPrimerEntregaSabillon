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
        Optional<Invoice> invoiceOpt = invoiceService.getInvoiceById(id);

        if (invoiceOpt.isPresent()) {
            InvoiceResponseDTO responseDTO = invoiceService.convertToDTO(invoiceOpt.get());
            return ResponseEntity.ok(responseDTO);
        } else {
            InvoiceResponseDTO errorResponse = new InvoiceResponseDTO();
            errorResponse.addError("Factura no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> guardarInvoice(@RequestBody InvoiceRequestDTO invoiceRequest) {
        InvoiceResponseDTO response = invoiceService.crearInvoice(invoiceRequest);

        // Si hay errores en la validación
        if (response.getErrores() != null && !response.getErrores().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Si todo ok
        return ResponseEntity.created(URI.create("/invoice/" + response.getId())).body(response);
    }
}