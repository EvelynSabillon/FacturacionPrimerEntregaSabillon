package edu.coderhouse.Jpa;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.coderhouse.Jpa.Cliente;
import edu.coderhouse.Jpa.Invoice;
import edu.coderhouse.Jpa.InvoiceDetails;
import edu.coderhouse.Jpa.Product;

@Service
public class DaoFactory {
    private final SessionFactory sessionFactory;

    @Autowired
    public DaoFactory(EntityManagerFactory factory) {
        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }

    // Método genérico para crear una entidad
    public void create(Object obj) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(obj);
        session.getTransaction().commit();
    }

    // Método genérico para actualizar una entidad
    public void update(Object obj) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(obj);
        session.getTransaction().commit();
    }

    // Obtener un cliente por ID
    public Cliente getCliente(int id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Cliente cliente = session.get(Cliente.class, id);
        session.getTransaction().commit();
        return cliente;
    }

    // Obtener una factura por ID
    public Invoice getInvoice(int id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Invoice invoice = session.get(Invoice.class, id);
        session.getTransaction().commit();
        return invoice;
    }

    // Obtener un detalle de factura por ID
    public InvoiceDetails getInvoiceDetails(int id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        InvoiceDetails invoiceDetails = session.get(InvoiceDetails.class, id);
        session.getTransaction().commit();
        return invoiceDetails;
    }

    // Obtener un producto por ID
    public Product getProduct(int id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Product product = session.get(Product.class, id);
        session.getTransaction().commit();
        return product;
    }
}