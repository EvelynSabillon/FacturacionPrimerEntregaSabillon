package edu.coderhouse.Jpa;

import java.util.List;

public class InvoiceRequestDTO {
    private ClienteDTO cliente;
    private List<LineaDTO> lineas;

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public List<LineaDTO> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaDTO> lineas) {
        this.lineas = lineas;
    }

    public static class ClienteDTO {
        private int clienteid;

        public int getClienteid() {
            return clienteid;
        }

        public void setClienteid(int clienteid) {
            this.clienteid = clienteid;
        }
    }

    public static class LineaDTO {
        private int cantidad;
        private ProductoDTO producto;

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public ProductoDTO getProducto() {
            return producto;
        }

        public void setProducto(ProductoDTO producto) {
            this.producto = producto;
        }
    }

    public static class ProductoDTO {
        private int productoid;

        public int getProductoid() {
            return productoid;
        }

        public void setProductoid(int productoid) {
            this.productoid = productoid;
        }
    }
}
