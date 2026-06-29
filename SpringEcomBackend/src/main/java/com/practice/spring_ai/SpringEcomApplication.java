package com.practice.spring_ai;

import com.practice.spring_ai.model.Product;
import com.practice.spring_ai.repo.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@SpringBootApplication
public class SpringEcomApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringEcomApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringEcomApplication.class, args);
	}

	@Bean
	CommandLineRunner seedDefaultProducts(ProductRepo productRepo) {
		return args -> {
			if (productRepo.count() > 0) {
				log.info("Skipping default product seed because product table is not empty.");
				return;
			}

			byte[] defaultImage;
			try {
				defaultImage = new ClassPathResource("default-product-image.jpeg").getInputStream().readAllBytes();
			} catch (Exception exception) {
				log.warn("Failed to load default-product-image.jpeg. Seeding products without image bytes.", exception);
				defaultImage = new byte[0];
			}

			List<Product> defaults = List.of(
					createProduct("iPhone 14", "Latest Apple iPhone", "Apple", new BigDecimal("999.99"), "Mobile", "2023-01-01",  50, defaultImage),
					createProduct("Galaxy S22", "Latest Samsung Galaxy", "Samsung", new BigDecimal("899.99"), "Mobile", "2023-02-01", 30, defaultImage),
					createProduct("MacBook Pro", "Apple MacBook Pro 16-inch", "Apple", new BigDecimal("2399.99"), "Laptop", "2023-03-01", 20, defaultImage),
					createProduct("Dell XPS 13", "Dell XPS 13 Ultrabook", "Dell", new BigDecimal("1299.99"), "Laptop", "2023-04-01",  25, defaultImage),
					createProduct("Levi Jeans", "Classic Levi Jeans", "Levi", new BigDecimal("1599.99"), "Fashion", "2023-05-01", 100, defaultImage)
			);

			productRepo.saveAll(defaults);
			log.info("Seeded {} default products.", defaults.size());
		};
	}

	private Product createProduct(
			String name,
			String description,
			String brand,
			BigDecimal price,
			String category,
			String releaseDate,
			int stockQuantity,
			byte[] imageBytes
	) {
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setBrand(brand);
		product.setPrice(price);
		product.setCategory(category);
		product.setReleaseDate(Date.valueOf(releaseDate));
		product.setProductAvailable(true);
		product.setStockQuantity(stockQuantity);
		product.setImageName("default-product-image.jpeg");
		product.setImageType("image/jpeg");
		product.setProductImage(imageBytes);
		return product;
	}
}
