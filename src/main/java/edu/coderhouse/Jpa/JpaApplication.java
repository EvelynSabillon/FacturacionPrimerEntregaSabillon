package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.coderhouse.Jpa.Cliente;
import edu.coderhouse.Jpa.Invoice;
import edu.coderhouse.Jpa.InvoiceDetails;
import edu.coderhouse.Jpa.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class JpaApplication implements CommandLineRunner {

	@Autowired
	private DaoFactory daoFactory;

	public static void main(String[] args) {
		SpringApplication.run(JpaApplication.class, args);
		System.out.println("Aplicación levantada");
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			// Se crea un Cliente
			Cliente cliente = new Cliente("Ernesto", "Sabato", "12345678");
			daoFactory.create(cliente);

			// Se crean productos
			Product product1 = new Product("Laptop Lenovo", "LEN123", 10, 1200.00);
			Product product2 = new Product("Mouse Logitech", "MOU456", 50, 25.99);
			daoFactory.create(product1);
			daoFactory.create(product2);

			// Se crea una factura para el cliente
			Invoice invoice = new Invoice(cliente, new Date(), 0.0);
			daoFactory.create(invoice);

			// Se crean detalles de factura con los productos comprados
			InvoiceDetails detail1 = new InvoiceDetails(invoice, product1, 1, product1.getPrice());
			InvoiceDetails detail2 = new InvoiceDetails(invoice, product2, 2, product2.getPrice() * 2);
			daoFactory.create(detail1);
			daoFactory.create(detail2);

			// Actualizar el total de la factura
			invoice.setTotal(detail1.getPrice() + detail2.getPrice());
			daoFactory.update(invoice);

			// Obtener y mostrar la factura guardada
			Invoice invoiceGuardada = daoFactory.getInvoice(invoice.getId());
			imprimirFactura(invoiceGuardada, "Factura Guardada: ");

			// Modificar la factura agregando otro producto
			modificarFactura(invoiceGuardada, new Product("Teclado Mecánico", "KEY789", 20, 80.50));

			// Obtener y mostrar la factura modificada
			Invoice invoiceModificada = daoFactory.getInvoice(invoiceGuardada.getId());
			imprimirFactura(invoiceModificada, "Factura Modificada: ");

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	private void modificarFactura(Invoice invoice, Product nuevoProducto) {
		// Guardar el nuevo producto
		daoFactory.create(nuevoProducto);

		// Crear un nuevo detalle de factura
		InvoiceDetails nuevoDetalle = new InvoiceDetails(invoice, nuevoProducto, 1, nuevoProducto.getPrice());
		daoFactory.create(nuevoDetalle);

		// Actualizar el total de la factura
		invoice.setTotal(invoice.getTotal() + nuevoDetalle.getPrice());
		daoFactory.update(invoice);
	}

	private void imprimirFactura(Invoice invoice, String mensaje) {
		System.out.println("----------------------------");
		System.out.println(mensaje + " ID: " + invoice.getId() + ", Cliente: " + invoice.getClient().getName() + " " + invoice.getClient().getLastname() + ", Total: " + invoice.getTotal());
	}
}