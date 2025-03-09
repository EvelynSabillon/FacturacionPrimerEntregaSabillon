package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice crearInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> getInvoiceById(int id) {
        return invoiceRepository.findById(id);
    }
}