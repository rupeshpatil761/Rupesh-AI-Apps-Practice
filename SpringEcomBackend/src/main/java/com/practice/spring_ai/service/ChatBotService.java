package com.practice.spring_ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatBotService {

    private static final Logger log = LoggerFactory.getLogger(ChatBotService.class);
    private static final String PROMPT_PATH = "prompts/chatbot-rag-prompt.st";

    private final ChatClient claudeChatClient;
    private final VectorStoreService vectorStoreService;

    public ChatBotService(
            @Qualifier("claudeChatClient") ChatClient claudeChatClient,
            VectorStoreService vectorStoreService) {
        this.claudeChatClient = claudeChatClient;
        this.vectorStoreService = vectorStoreService;
    }

    public String getBotResponse(String userQuery) {
        try {
            String promptStringTemplate = loadPromptTemplate();
            String context = vectorStoreService.fetchSemanticContext(userQuery);
            log.info("Fetched context for userQuery '{}': {}", userQuery, context);
            Map<String, Object> variables = new HashMap<>();
            variables.put("userQuery", userQuery);
            variables.put("context", context);

            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .template(promptStringTemplate)
                    .variables(variables)
                    .build();

            return claudeChatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception exception) {
            log.error("ChatBotService failed while generating response for query='{}'", userQuery, exception);
            return "Bot Failed "+ exception.getMessage();
        }
    }

    private String loadPromptTemplate() throws Exception {
        ClassPathResource promptResource = new ClassPathResource(PROMPT_PATH);
        return new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
