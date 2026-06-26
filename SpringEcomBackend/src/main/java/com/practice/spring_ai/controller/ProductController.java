package com.practice.spring_ai.controller;

import com.practice.spring_ai.model.Product;
import com.practice.spring_ai.service.AiChatService;
import com.practice.spring_ai.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AiChatService aiChatService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(){
        log.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        log.debug("Returning {} products", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id){
        log.info("Fetching product with id={}", id);
        Product product = productService.getProductById(id);

        if(product.getId() > 0) {
            log.debug("Product found: id={}", id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            log.warn("Product not found: id={}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("product/{productId}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int productId){
        log.info("Fetching image for productId={}", productId);
        Product product = productService.getProductById(productId);
        if(product.getId() > 0) {
            log.debug("Image found for productId={}", productId);
            return new ResponseEntity<>(product.getProductImage(), HttpStatus.OK);
        } else {
            log.warn("Image not found for productId={}", productId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile){
        log.info("Adding new product: name={}", product.getName());
        try {
            Product savedProduct = productService.addOrUpdateProduct(product, imageFile);
            log.info("Product added successfully: id={}", savedProduct.getId());
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error("Failed to add product: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id, @RequestPart Product product, @RequestPart MultipartFile imageFile){
        log.info("Updating product with id={}", id);
        try {
            productService.addOrUpdateProduct(product, imageFile);
            log.info("Product updated successfully: id={}", id);
            return new ResponseEntity<>("Updated", HttpStatus.OK);
        } catch (IOException e) {
            log.error("Failed to update product id={}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id){
        log.info("Deleting product with id={}", id);
        Product product = productService.getProductById(id);
        if(product != null){
            productService.deleteProduct(id);
            log.info("Product deleted successfully: id={}", id);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } else {
            log.warn("Cannot delete — product not found: id={}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
        log.info("Searching products with keyword='{}'", keyword);
        List<Product> products = productService.searchProducts(keyword);
        log.debug("Search returned {} result(s) for keyword='{}'", products.size(), keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/product/generate-description")
    public ResponseEntity<String> generateProductDesc(@RequestParam String productName, @RequestParam String category) {
        log.info("Generating AI description for product='{}', category='{}'", productName, category);
        try {
            String description = aiChatService.generateProductDescription(productName, category);
            log.debug("AI description generated for product='{}'", productName);
            return new ResponseEntity<>(description, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to generate description for product='{}': {}", productName, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
