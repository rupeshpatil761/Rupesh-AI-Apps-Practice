package com.practice.spring_ai.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // When using single AI provider, you can directly autowire the ChatClient without needing a builder.
    /*@Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }*/

    @Bean("claudeChatClient")
    public ChatClient claudeChatClient(AnthropicChatModel anthropicChatModel) {
        return ChatClient.create(anthropicChatModel);
    }

    @Bean("openAiImageProvider")
    public ImageModel openAiImageProvider(OpenAiImageModel openAiImageModel) {
        return openAiImageModel;
    }
}
