package com.practice.spring_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiChatService {

    @Autowired
    private ChatClient chatClient;

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
            desc = chatClient.prompt(descPrompt)
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

}
