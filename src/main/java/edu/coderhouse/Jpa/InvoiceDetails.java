package edu.coderhouse.Jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "INVOICE_DETAILS")
public class InvoiceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVOICE_DETAIL_ID")
    private int invoiceDetailId;

    @ManyToOne
    @JoinColumn(name = "INVOICE_ID", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "AMOUNT", nullable = false)
    private int amount;

    @Column(name = "PRICE", nullable = false)
    private double price;

    public InvoiceDetails() {}

    public InvoiceDetails(Invoice invoice, Product product, int amount, double price) {
        this.invoice = invoice;
        this.product = product;
        this.amount = amount;
        this.price = price;
    }

    public int getInvoiceDetailId() {
        return invoiceDetailId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
