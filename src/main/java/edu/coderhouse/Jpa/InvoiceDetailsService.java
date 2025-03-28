package edu.coderhouse.Jpa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class InvoiceDetailsService {
    @Autowired
    private InvoiceDetailsRepository invoiceDetailsRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductRepository productRepository;

    public InvoiceDetails crearInvoiceDetails(InvoiceDetails invoiceDetails) throws Exception {
        // 1. Asegúrate de que la factura exista
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceDetails.getInvoice().getId());
        if (invoiceOpt.isEmpty()) {
            throw new Exception("Factura no encontrada");
        }

        // 2. Asigna la factura al detalle
        invoiceDetails.setInvoice(invoiceOpt.get());

        // 3. Verifica que el producto exista
        Optional<Product> productOpt = productRepository.findById(invoiceDetails.getProduct().getId());
        if (productOpt.isEmpty()) {
            throw new Exception("Producto no encontrado");
        }

        // 4. Guardar el detalle de la factura
        return invoiceDetailsRepository.save(invoiceDetails);
    }

    public Optional<InvoiceDetails> getInvoiceDetailsById(int id) {
        // Buscar el detalle de la factura por ID
        return invoiceDetailsRepository.findById(id);
    }
}