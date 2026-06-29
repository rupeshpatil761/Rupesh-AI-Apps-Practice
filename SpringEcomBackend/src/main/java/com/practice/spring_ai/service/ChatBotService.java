package com.practice.spring_ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatBotService {

    private static final Logger log = LoggerFactory.getLogger(ChatBotService.class);
    private static final String PROMPT_PATH = "prompts/chatbot-rag-prompt.st";

    private final ChatClient claudeChatClient;
    private final PgVectorStore vectorStore;

    public ChatBotService(
            @Qualifier("claudeChatClient") ChatClient claudeChatClient,
            PgVectorStore vectorStore) {
        this.claudeChatClient = claudeChatClient;
        this.vectorStore = vectorStore;
    }

    public String getBotResponse(String userQuery) {
        try {
            String promptStringTemplate = loadPromptTemplate();
            String context = fetchSemanticContext(userQuery);
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

    private String fetchSemanticContext(String userQuery) {
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

    private String loadPromptTemplate() throws Exception {
        ClassPathResource promptResource = new ClassPathResource(PROMPT_PATH);
        return new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
