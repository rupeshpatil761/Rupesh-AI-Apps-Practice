package com.practice.spring_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Service
public class AiService {

    private final ChatClient claudeChatClient;
    private final ImageModel openAiImageModel;

    public AiService(
            @Qualifier("claudeChatClient") ChatClient claudeChatClient,
            @Qualifier("openAiImageModel") ImageModel openAiImageModel) {
        this.claudeChatClient = claudeChatClient;
        this.openAiImageModel = openAiImageModel;
    }

    public String generateProductDescription(String name, String category) {
        log.info("Generating product description for name='{}', category='{}'", name, category);

        String descPrompt = String.format("""
                Write a concise and professional product description for an e-commerce listing.
            
                Product Name: %s
                Category: %s

                Keep it simple, engaging, and highlight its primary features or benefits.
                Avoid technical jargon and keep it customer-friendly.
                Limit the description to 250 characters maximum.
                
                """, name, category);

        String desc = "Default description";
        try {
            desc = claudeChatClient.prompt(descPrompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            log.debug("Generated description for name='{}': {}", name, desc);
        } catch (Exception e) {
            log.error("Error generating product description for name='{}': {}. Using default description.", name, e.getMessage(), e);
        }
        return desc;
    }

    public byte[] generateProductImage(String name, String category, String description) {
        log.info("Generating product image for name='{}', category='{}'", name, category);
        String imagePrompt = String.format("""
            Generate a highly realistic, professional-grade e-commerce product image.
            
                 Product Details:
                 - Category: %s
                 - Name: '%s'
                 - Description: %s
            
                 Requirements:
                 - Use a clean, minimalistic, white or very light grey background.
                 - Ensure the product is well-lit with soft, natural-looking lighting.
                 - Add realistic shadows and soft reflections to ground the product naturally.
                 - No humans, brand logos, watermarks, or text overlays should be visible.
                 - Showcase the product from its most flattering angle that highlights key features.
                 - Ensure the product occupies a prominent position in the frame, centered or slightly off-centered.
                 - Maintain a high resolution and sharpness, ensuring all textures, colors, and details are clear.
                 - Follow the typical visual style of top e-commerce websites like Amazon, Flipkart, or Shopify.
                 - Make the product appear life-like and professionally photographed in a studio setup.
                 - The final image should look immediately ready for use on an e-commerce website without further editing.
                 """, category, name, description);

        try {
            // image options for anthropics model
            var imageOptions = ImageOptionsBuilder.builder()
                    .width(1024)
                    .height(1024)
                    .n(1)
                    .responseFormat("url")
                    .build();
            ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(imagePrompt, imageOptions));
            String imageUrl = imageResponse.getResult().getOutput().getUrl();
            log.debug("Generated image url for name='{}', url={}", name, imageUrl);
            return new URL(imageUrl).openStream().readAllBytes();
        } catch (Exception e) {
            log.error("Error generating product image for name='{}': {}. Returning default image.", name, e.getMessage(), e);
            return loadDefaultImage();
        }
    }

    private byte[] loadDefaultImage() {
        try {
            ClassPathResource defaultImage = new ClassPathResource("default-product-image.jpeg");
            return defaultImage.getInputStream().readAllBytes();
        } catch (IOException ioException) {
            log.error("Failed to load default image from resources/default-product-image.jpeg", ioException);
            return new byte[0];
        }
    }
}
