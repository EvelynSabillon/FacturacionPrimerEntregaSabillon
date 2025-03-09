package edu.coderhouse.Jpa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class InvoiceDetailsService {
    @Autowired
    private InvoiceDetailsRepository invoiceDetailsRepository;

    public InvoiceDetails crearInvoiceDetails(InvoiceDetails invoiceDetails) {
        return invoiceDetailsRepository.save(invoiceDetails);
    }

    public Optional<InvoiceDetails> getInvoiceDetailsById(int id) {
        return invoiceDetailsRepository.findById(id);
    }
}