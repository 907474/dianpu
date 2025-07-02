package com.myapp.aw.store.service;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.repository.ProductRepository;

import java.util.Optional;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product restockProduct(long productId, int amountToAdd) throws Exception {
        if (amountToAdd <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive.");
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new Exception("Product with ID " + productId + " not found.");
        }

        Product product = productOpt.get();
        product.setStock(product.getStock() + amountToAdd);
        return productRepository.save(product);
    }

    public Product updateProduct(long productId, String sku, String name, double price, int stock) throws Exception {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new Exception("Product with ID " + productId + " not found.");
        }

        Product product = productOpt.get();
        product.setSku(sku);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        return productRepository.save(product);
    }
}
