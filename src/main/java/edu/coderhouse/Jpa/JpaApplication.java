package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class JpaApplication implements CommandLineRunner {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ProductService productService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceDetailsService invoiceDetailsService;

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

			// Se crean productos
			Product product1 = new Product("Laptop Lenovo", "LEN123", 10, 1200.00);
			Product product2 = new Product("Mouse Logitech", "MOU456", 50, 25.99);
			product1 = productService.crearProduct(product1);
			product2 = productService.crearProduct(product2);

			// Se crea una factura para el cliente
			Invoice invoice = new Invoice(cliente, new Date(), 0.0);
			invoice = invoiceService.crearInvoice(invoice);

			// Se crean detalles de factura con los productos comprados
			InvoiceDetails detail1 = new InvoiceDetails(invoice, product1, 1, product1.getPrice());
			InvoiceDetails detail2 = new InvoiceDetails(invoice, product2, 2, product2.getPrice() * 2);
			invoiceDetailsService.crearInvoiceDetails(detail1);
			invoiceDetailsService.crearInvoiceDetails(detail2);

			// Actualizar el total de la factura
			double totalFactura = detail1.getPrice() + detail2.getPrice();
			invoice.setTotal(totalFactura);
			invoiceService.crearInvoice(invoice);

			// Obtener y mostrar la factura guardada
			Invoice invoiceGuardada = invoiceService.getInvoiceById(invoice.getId()).orElse(null);
			imprimirFactura(invoiceGuardada, "Factura Guardada: ");

			// Modificar la factura agregando otro producto
			modificarFactura(invoiceGuardada, new Product("Teclado Mecánico", "KEY789", 20, 80.50));

			// Obtener y mostrar la factura modificada
            Invoice invoiceModificada = null;
            if (invoiceGuardada != null) {
                invoiceModificada = invoiceService.getInvoiceById(invoiceGuardada.getId()).orElse(null);
            }
            imprimirFactura(invoiceModificada, "Factura Modificada: ");

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	private void modificarFactura(Invoice invoice, Product nuevoProducto) {
		// Guardar el nuevo producto
		nuevoProducto = productService.crearProduct(nuevoProducto);

		// Crear un nuevo detalle de factura
		InvoiceDetails nuevoDetalle = new InvoiceDetails(invoice, nuevoProducto, 1, nuevoProducto.getPrice());
		invoiceDetailsService.crearInvoiceDetails(nuevoDetalle);

		// Actualizar el total de la factura
		invoice.setTotal(invoice.getTotal() + nuevoDetalle.getPrice());
		invoiceService.crearInvoice(invoice);
	}

	private void imprimirFactura(Invoice invoice, String mensaje) {
		if (invoice != null) {
			System.out.println("----------------------------");
			System.out.println(mensaje + " ID: " + invoice.getId() +
					", Cliente: " + invoice.getClient().getName() + " " + invoice.getClient().getLastname() +
					", Total: " + invoice.getTotal());
		} else {
			System.out.println(mensaje + " No encontrada");
		}
	}
}