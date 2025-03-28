package edu.coderhouse.Jpa;

import edu.coderhouse.Jpa.Invoice;
import edu.coderhouse.Jpa.InvoiceDetails;
import edu.coderhouse.Jpa.Cliente;
import edu.coderhouse.Jpa.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceApiRest invoiceApiRest;

    @Transactional
    public Invoice crearInvoice(Invoice invoice) throws Exception {


        // 1. Validar que el cliente exista
        Optional<Cliente> clienteOpt = clienteRepository.findById(invoice.getClient().getId());
        Cliente cliente = clienteOpt.orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        invoice.setClient(cliente);

        // Verificar si la lista de detalles de factura es null
        if (invoice.getInvoiceDetails() == null) {
            invoice.setInvoiceDetails(new ArrayList<>()); // Inicializar la lista si es null
        }

        // 2. Validar productos y stock
        double total = 0;
        int totalProductosVendidos = 0;

        Invoice savedInvoice = invoiceRepository.save(invoice);

        for (InvoiceDetails detalle : savedInvoice.getInvoiceDetails()) {
            // Establecer la referencia de la factura en cada detalle
            detalle.setInvoice(savedInvoice);

            // Verificar si el producto existe
            Optional<Product> productOpt = productRepository.findById(detalle.getProduct().getId());
            if (productOpt.isEmpty()) {
                throw new Exception("El producto con ID " + detalle.getProduct().getId() + " no existe.");
            }

            Product producto = productOpt.get();

            if (detalle.getAmount() > producto.getStock()) {
                throw new Exception("Stock insuficiente para el producto: " + producto.getDescription());
            }

            // 3. Mantener precio histórico
            detalle.setPrice(producto.getPrice());

            // 4. Reducir stock
            producto.setStock(producto.getStock() - detalle.getAmount());
            productRepository.save(producto);

            // Calcular total de la factura
            total += detalle.getAmount() * producto.getPrice();
            totalProductosVendidos += detalle.getAmount();
        }

        // 5. Obtener fecha del servicio externo
        String fechaApi = invoiceApiRest.obtenerFechaUTC();
        Date fechaFinal = null;

        if (fechaApi != null) {
            try {
                // Usar un SimpleDateFormat que soporte la 'Z' para UTC
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                fechaFinal = dateFormat.parse(fechaApi); // Convertir la fecha de la API a Date
            } catch (Exception e) {
                e.printStackTrace();
                fechaFinal = new Date(); // Si hay un error en el parseo, usar la fecha actual
            }
        } else {
            fechaFinal = new Date(); // Usar la fecha actual si la API falla
        }

        // 6. Guardar factura con los datos finales
        invoice.setCreatedAt(fechaFinal);
        invoice.setTotal(total);

        // 5. Guardar la factura con los detalles
        // Persistir los detalles de la factura ahora que la relación se ha establecido
        for (InvoiceDetails detalle : savedInvoice.getInvoiceDetails()) {
            detalle.setInvoice(savedInvoice); // Asegurarse de que el detalle tenga la factura asociada
        }

        return invoiceRepository.save(invoice);
    }


    public Optional<Invoice> getInvoiceById(int id) {
        return invoiceRepository.findById(id);
    }
}