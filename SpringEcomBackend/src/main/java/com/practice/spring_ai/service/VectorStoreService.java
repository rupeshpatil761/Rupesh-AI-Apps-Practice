package com.practice.spring_ai.service;

import com.practice.spring_ai.model.Order;
import com.practice.spring_ai.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VectorStoreService {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreService.class);

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String fetchSemanticContext(String userQuery) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userQuery)
                        .topK(5)
                        .similarityThreshold(0.7)
                        .build()
        );
        return documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n", "", "\n"));
    }

    public void storeProductEmbeddingInVectorStore(Product savedProduct) {
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

    public void deleteEmbeddingFromVectorStore(int productId) {
        String filter = "productId == '" + productId + "'";
        log.info("Deleting vector embedding using filter: {}", filter);
        vectorStore.delete(filter);
        log.info("Deleted vector embedding for product id={}", productId);
    }

    public void storeOrderEmbeddingInVectorStore(Order savedOrder) {
        log.info("Storing vector embedding for order id={}", savedOrder.getId());
        try {
            String content = String.format("""
                    Order Summary:
                    Order ID: %s
                    Customer Name: %s
                    Email: %s
                    Status: %s
                    Order Date: %s
                    Products Ordered: %s
                    """,
                    savedOrder.getOrderId(),
                    savedOrder.getCustomerName(),
                    savedOrder.getEmail(),
                    savedOrder.getStatus(),
                    savedOrder.getOrderDate(),
                    savedOrder.getOrderItems().stream()
                            .map(item -> String.format("%s (Quantity: %d, Total Price: %.2f)",
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getTotalPrice()))
                            .collect(Collectors.joining(", "))
            );

            Document document = Document.builder()
                    .id(UUID.randomUUID().toString())
                    .text(content)
                    .metadata("orderId", String.valueOf(savedOrder.getId()))
                    .build();

            vectorStore.add(List.of(document));
            log.info("Vector embedding stored for order id={}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Error storing vector embedding for order id={}: {}", savedOrder.getId(), e.getMessage());
        }
    }
}
