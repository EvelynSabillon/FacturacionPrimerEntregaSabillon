package edu.coderhouse.Jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product crearProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }
}