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

import edu.coderhouse.Jpa.InvoiceRequestDTO;
import edu.coderhouse.Jpa.InvoiceResponseDTO;

import org.springframework.web.client.RestClientException;

import java.text.SimpleDateFormat;
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

    private static final String WORLD_CLOCK_API_URL = "http://worldclockapi.com/api/json/utc/now";

    // Método para obtener la fecha actual desde la API o sistema
    private Date obtenerFechaActual() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            WorldClockResponse response = restTemplate.getForObject(WORLD_CLOCK_API_URL, WorldClockResponse.class);
            if (response != null && response.getCurrentDateTime() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                return dateFormat.parse(response.getCurrentDateTime());
            }
        } catch (RestClientException | java.text.ParseException e) {
            // Si falla la API o el parseo, usar fecha local
        }
        // Si algo falla, retornamos la fecha actual
        return new Date();
    }

    // Clase interna para mapear la respuesta de la API
    public static class WorldClockResponse {
        private String currentDateTime;

        public String getCurrentDateTime() {
            return currentDateTime;
        }

        public void setCurrentDateTime(String currentDateTime) {
            this.currentDateTime = currentDateTime;
        }
    }

    @Transactional
    public InvoiceResponseDTO crearInvoice(InvoiceRequestDTO invoiceRequest) {
        InvoiceResponseDTO response = new InvoiceResponseDTO();
        List<Product> productosAModificar = new ArrayList<>();

        try {
            // 1. Validar que el cliente exista
            Optional<Cliente> clienteOpt = clienteRepository.findById(invoiceRequest.getCliente().getClienteid());
            if (clienteOpt.isEmpty()) {
                response.addError("Cliente con ID " + invoiceRequest.getCliente().getClienteid() + " no encontrado.");
                return response;
            }
            Cliente cliente = clienteOpt.get();

            // Inicializar factura y detalles
            Invoice invoice = new Invoice();
            invoice.setClient(cliente);
            invoice.setCreatedAt(obtenerFechaActual()); // Usar el método local
            invoice.setInvoiceDetails(new ArrayList<>());

            // Valores para total y cantidad
            double total = 0;
            int cantidadTotal = 0;

            // 2. Procesar cada línea del request
            boolean hayErrores = false;

            for (InvoiceRequestDTO.LineaDTO lineaDTO : invoiceRequest.getLineas()) {
                // 2.1 Validar que el producto exista
                Optional<Product> productoOpt = productRepository.findById(lineaDTO.getProducto().getProductoid());
                if (productoOpt.isEmpty()) {
                    response.addError("Producto con ID " + lineaDTO.getProducto().getProductoid() + " no encontrado.");
                    hayErrores = true;
                    continue;
                }

                Product producto = productoOpt.get();

                // 2.2 Validar stock suficiente
                if (lineaDTO.getCantidad() <= 0) {
                    response.addError("La cantidad para el producto " + producto.getDescription() + " debe ser mayor a 0.");
                    hayErrores = true;
                    continue;
                }

                if (lineaDTO.getCantidad() > producto.getStock()) {
                    response.addError("Stock insuficiente para el producto " + producto.getDescription() +
                            ". Solicitado: " + lineaDTO.getCantidad() + ", Disponible: " + producto.getStock());
                    hayErrores = true;
                    continue;
                }

                // 2.3 Añadir detalle a la factura
                InvoiceDetails detalle = new InvoiceDetails();
                detalle.setInvoice(invoice);
                detalle.setProduct(producto);
                detalle.setAmount(lineaDTO.getCantidad());
                detalle.setPrice(producto.getPrice()); // Mantiene el precio histórico

                invoice.getInvoiceDetails().add(detalle);

                // 2.4 Preparar para actualizar stock
                producto.setStock(producto.getStock() - lineaDTO.getCantidad());
                productosAModificar.add(producto);

                // 2.5 Actualizar contadores
                total += lineaDTO.getCantidad() * producto.getPrice();
                cantidadTotal += lineaDTO.getCantidad();
            }

            // Si hay errores, no continuamos con la creación de la factura
            if (hayErrores) {
                return response;
            }

            // 3. Finalizar la factura y guardarla
            invoice.setTotal(total);
            Invoice savedInvoice = invoiceRepository.save(invoice);

            // 4. Actualizar stock de productos
            for (Product producto : productosAModificar) {
                productRepository.save(producto);
            }

            // 5. Preparar la respuesta
            response.setId(savedInvoice.getId());
            response.setFecha(savedInvoice.getCreatedAt());
            response.setTotal(savedInvoice.getTotal());
            response.setCantidadProductos(cantidadTotal);

            // Información del cliente
            InvoiceResponseDTO.ClienteDTO clienteDTO = new InvoiceResponseDTO.ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getName());
            clienteDTO.setApellido(cliente.getLastname());
            clienteDTO.setDocumento(cliente.getDocNumber());
            response.setCliente(clienteDTO);

            // Detalle de líneas
            List<InvoiceResponseDTO.LineaDTO> lineasDTO = new ArrayList<>();
            for (InvoiceDetails detalle : savedInvoice.getInvoiceDetails()) {
                InvoiceResponseDTO.LineaDTO lineaDTO = new InvoiceResponseDTO.LineaDTO();
                lineaDTO.setCantidad(detalle.getAmount());
                lineaDTO.setPrecioUnitario(detalle.getPrice());
                lineaDTO.setSubtotal(detalle.getAmount() * detalle.getPrice());

                InvoiceResponseDTO.ProductoDTO productoDTO = new InvoiceResponseDTO.ProductoDTO();
                productoDTO.setId(detalle.getProduct().getId());
                productoDTO.setDescripcion(detalle.getProduct().getDescription());
                productoDTO.setCodigo(detalle.getProduct().getCode());
                lineaDTO.setProducto(productoDTO);

                lineasDTO.add(lineaDTO);
            }
            response.setLineas(lineasDTO);

        } catch (Exception e) {
            response.addError("Error al crear la factura: " + e.getMessage());
        }

        return response;
    }

    public Optional<Invoice> getInvoiceById(int id) {
        return invoiceRepository.findById(id);
    }

    public InvoiceResponseDTO convertToDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();

        dto.setId(invoice.getId());
        dto.setFecha(invoice.getCreatedAt());
        dto.setTotal(invoice.getTotal());

        // Calcular cantidad total de productos
        int cantidadTotal = invoice.getInvoiceDetails().stream()
                .mapToInt(InvoiceDetails::getAmount)
                .sum();
        dto.setCantidadProductos(cantidadTotal);

        // Información del cliente
        InvoiceResponseDTO.ClienteDTO clienteDTO = new InvoiceResponseDTO.ClienteDTO();
        clienteDTO.setId(invoice.getClient().getId());
        clienteDTO.setNombre(invoice.getClient().getName());
        clienteDTO.setApellido(invoice.getClient().getLastname());
        clienteDTO.setDocumento(invoice.getClient().getDocNumber());
        dto.setCliente(clienteDTO);

        // Detalle de líneas
        List<InvoiceResponseDTO.LineaDTO> lineasDTO = new ArrayList<>();
        for (InvoiceDetails detalle : invoice.getInvoiceDetails()) {
            InvoiceResponseDTO.LineaDTO lineaDTO = new InvoiceResponseDTO.LineaDTO();
            lineaDTO.setCantidad(detalle.getAmount());
            lineaDTO.setPrecioUnitario(detalle.getPrice());
            lineaDTO.setSubtotal(detalle.getAmount() * detalle.getPrice());

            InvoiceResponseDTO.ProductoDTO productoDTO = new InvoiceResponseDTO.ProductoDTO();
            productoDTO.setId(detalle.getProduct().getId());
            productoDTO.setDescripcion(detalle.getProduct().getDescription());
            productoDTO.setCodigo(detalle.getProduct().getCode());
            lineaDTO.setProducto(productoDTO);

            lineasDTO.add(lineaDTO);
        }
        dto.setLineas(lineasDTO);

        return dto;
    }
}