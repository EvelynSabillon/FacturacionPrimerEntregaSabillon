package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@SpringBootApplication
public class JpaApplication implements CommandLineRunner {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ProductService productService;

	@Autowired
	private InvoiceService invoiceService;

	public static void main(String[] args) {
		SpringApplication.run(JpaApplication.class, args);
		System.out.println("Aplicación levantada");
	}


	@Override
	public void run(String... args) throws Exception {
		try {
			// Se crea un Cliente
			Cliente cliente = new Cliente("Ernesto", "Sabato", "12345678");
			cliente = clienteService.crearCliente(cliente);
			System.out.println("Cliente creado con ID: " + cliente.getId());

			// Se crean productos
			Product product1 = new Product("Laptop Lenovo", "LEN123", 10, 1200.00);
			Product product2 = new Product("Mouse Logitech", "MOU456", 50, 25.99);
			product1 = productService.crearProduct(product1);
			product2 = productService.crearProduct(product2);
			System.out.println("Productos creados con IDs: " + product1.getId() + ", " + product2.getId());

			// Crear factura usando el nuevo DTO
			InvoiceRequestDTO invoiceRequest = crearInvoiceRequest(cliente.getId(), product1.getId(), product2.getId());

			try {
				InvoiceResponseDTO response = invoiceService.crearInvoice(invoiceRequest);

				if (response.getErrores() != null && !response.getErrores().isEmpty()) {
					System.out.println("Errores al crear la factura:");
					for (String error : response.getErrores()) {
						System.out.println("- " + error);
					}
				} else {
					System.out.println("----------------------------");
					System.out.println("Factura creada exitosamente:");
					System.out.println("ID: " + response.getId());
					System.out.println("Fecha: " + response.getFecha());
					System.out.println("Cliente: " + response.getCliente().getNombre() + " " + response.getCliente().getApellido());
					System.out.println("Total: $" + response.getTotal());
					System.out.println("Cantidad de productos: " + response.getCantidadProductos());
					System.out.println("----------------------------");
					System.out.println("Detalle de productos:");

					for (InvoiceResponseDTO.LineaDTO linea : response.getLineas()) {
						System.out.println("- " + linea.getProducto().getDescripcion() +
								" (x" + linea.getCantidad() + ") - $" + linea.getSubtotal());
					}

					// Modificar la factura agregando otro producto
					Product product3 = new Product("Teclado Mecánico", "KEY789", 20, 80.50);
					product3 = productService.crearProduct(product3);

					// Crear nuevo request para agregar un producto
					InvoiceRequestDTO secondRequest = crearInvoiceRequestParaProductoAdicional(
							cliente.getId(), product3.getId());

					InvoiceResponseDTO responseModificada = invoiceService.crearInvoice(secondRequest);

					if (responseModificada.getErrores() != null && !responseModificada.getErrores().isEmpty()) {
						System.out.println("Errores al modificar la factura:");
						for (String error : responseModificada.getErrores()) {
							System.out.println("- " + error);
						}
					} else {
						System.out.println("----------------------------");
						System.out.println("Factura adicional creada:");
						System.out.println("ID: " + responseModificada.getId());
						System.out.println("Fecha: " + responseModificada.getFecha());
						System.out.println("Cliente: " + responseModificada.getCliente().getNombre() + " " +
								responseModificada.getCliente().getApellido());
						System.out.println("Total: $" + responseModificada.getTotal());
						System.out.println("Cantidad de productos: " + responseModificada.getCantidadProductos());
						System.out.println("----------------------------");
						System.out.println("Detalle de productos:");

						for (InvoiceResponseDTO.LineaDTO linea : responseModificada.getLineas()) {
							System.out.println("- " + linea.getProducto().getDescripcion() +
									" (x" + linea.getCantidad() + ") - $" + linea.getSubtotal());
						}
					}
				}

			} catch (Exception e) {
				System.out.println("Error inesperado al crear la factura: " + e.getMessage());
				e.printStackTrace();
			}

		} catch (Exception ex) {
			System.out.println("Error general: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private InvoiceRequestDTO crearInvoiceRequest(int clienteId, int productoId1, int productoId2) {
		InvoiceRequestDTO request = new InvoiceRequestDTO();

		// Configurar cliente
		InvoiceRequestDTO.ClienteDTO clienteDTO = new InvoiceRequestDTO.ClienteDTO();
		clienteDTO.setClienteid(clienteId);
		request.setCliente(clienteDTO);

		// Configurar líneas de productos
		List<InvoiceRequestDTO.LineaDTO> lineas = new ArrayList<>();

		// Línea 1
		InvoiceRequestDTO.LineaDTO linea1 = new InvoiceRequestDTO.LineaDTO();
		linea1.setCantidad(1);
		InvoiceRequestDTO.ProductoDTO producto1 = new InvoiceRequestDTO.ProductoDTO();
		producto1.setProductoid(productoId1);
		linea1.setProducto(producto1);
		lineas.add(linea1);

		// Línea 2
		InvoiceRequestDTO.LineaDTO linea2 = new InvoiceRequestDTO.LineaDTO();
		linea2.setCantidad(2);
		InvoiceRequestDTO.ProductoDTO producto2 = new InvoiceRequestDTO.ProductoDTO();
		producto2.setProductoid(productoId2);
		linea2.setProducto(producto2);
		lineas.add(linea2);

		request.setLineas(lineas);

		return request;
	}

	private InvoiceRequestDTO crearInvoiceRequestParaProductoAdicional(int clienteId, int productoId) {
		InvoiceRequestDTO request = new InvoiceRequestDTO();

		// Configurar cliente
		InvoiceRequestDTO.ClienteDTO clienteDTO = new InvoiceRequestDTO.ClienteDTO();
		clienteDTO.setClienteid(clienteId);
		request.setCliente(clienteDTO);

		// Configurar líneas de productos
		List<InvoiceRequestDTO.LineaDTO> lineas = new ArrayList<>();

		// Línea para el nuevo producto
		InvoiceRequestDTO.LineaDTO linea = new InvoiceRequestDTO.LineaDTO();
		linea.setCantidad(1);
		InvoiceRequestDTO.ProductoDTO producto = new InvoiceRequestDTO.ProductoDTO();
		producto.setProductoid(productoId);
		linea.setProducto(producto);
		lineas.add(linea);

		request.setLineas(lineas);

		return request;
	}
}