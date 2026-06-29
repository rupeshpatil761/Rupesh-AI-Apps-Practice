package com.practice.spring_ai.service;

import com.practice.spring_ai.model.Product;
import com.practice.spring_ai.repo.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private VectorStore vectorStore;

    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productRepo.findAll();
        log.debug("Fetched {} products", products.size());
        return products;
    }

    public Product getProductById(int id) {
        log.info("Fetching product with id={}", id);
        Product product = productRepo.findById(id).orElse(new Product(-1));
        if (product.getId() == -1) {
            log.warn("Product not found for id={}", id);
        } else {
            log.debug("Found product id={}, name='{}'", product.getId(), product.getName());
        }
        return product;
    }

    public Product addOrUpdateProduct(Product product, MultipartFile image) throws IOException {
        log.info("Saving product name='{}', image='{}'", product.getName(), image.getOriginalFilename());

        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setProductImage(image.getBytes());

        Product savedProduct = productRepo.save(product);
        log.info("Product saved or updated successfully with id={}", savedProduct.getId());

        storeEmbeddingInVectorStore(savedProduct);

        return savedProduct;
    }

    private void storeEmbeddingInVectorStore(Product savedProduct) {
        log.info("Storing vector embedding for product id={}, name='{}'", savedProduct.getId(), savedProduct.getName());
        try {
            String content = String.format("""
                    Product Name: %s
                    Description: %s
                    Brand: %s
                    Category: %s
                    Price: %.2f
                    Release Date: %s
                    Available: %s
                    Stock: %s
                    """,
                    savedProduct.getName(),
                    savedProduct.getDescription(),
                    savedProduct.getBrand(),
                    savedProduct.getCategory(),
                    savedProduct.getPrice(),
                    savedProduct.getReleaseDate(),
                    savedProduct.isProductAvailable(),
                    savedProduct.getStockQuantity()
            );

            Document document = Document.builder()
                    .id(UUID.randomUUID().toString())
                    .text(content)
                    .metadata("productId", String.valueOf(savedProduct.getId()))
                    .build();

            vectorStore.add(List.of(document));
            log.info("Vector embedding stored for product id={}", savedProduct.getId());
        } catch (Exception e) {
            log.error("Error storing vector embedding for product id={}: {}", savedProduct.getId(), e.getMessage());
        }
    }

    public void deleteProduct(int id) {
        log.info("Deleting product with id={}", id);
        productRepo.deleteById(id);
        log.info("Product id={} deleted successfully", id);
    }

    @Transactional
    public List<Product> searchProducts(String keyword) {
        log.info("Searching products with keyword='{}'", keyword);
        List<Product> results = productRepo.searchProducts(keyword);
        log.debug("Search for keyword='{}' returned {} result(s)", keyword, results.size());
        return results;
    }
}
