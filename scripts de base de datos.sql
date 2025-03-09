
--BASE DE DATOS :  jdbc:h2:file:./data/coderHouse

-- Tabla de clientes
CREATE TABLE clients (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(75) NOT NULL,
    lastname VARCHAR(75) NOT NULL,
    docnumber VARCHAR(11) NOT NULL,
    PRIMARY KEY (id)
);

-- Tabla de productos
CREATE TABLE products (
    id INT NOT NULL AUTO_INCREMENT,
    description VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL,
    stock INT NOT NULL,
    price DOUBLE NOT NULL,
    PRIMARY KEY (id)
);

-- Tabla de facturas
CREATE TABLE invoice (
    id INT NOT NULL AUTO_INCREMENT,
    client_id INT NOT NULL,
    created_at DATETIME NOT NULL,
    total DOUBLE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- Tabla de detalles de factura
CREATE TABLE invoice_details (
    invoice_id INT NOT NULL,
    invoice_detail_id INT NOT NULL AUTO_INCREMENT,
    amount INT NOT NULL,
    product_id INT NOT NULL,
    price DOUBLE NOT NULL,
    PRIMARY KEY (invoice_detail_id),
    FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
