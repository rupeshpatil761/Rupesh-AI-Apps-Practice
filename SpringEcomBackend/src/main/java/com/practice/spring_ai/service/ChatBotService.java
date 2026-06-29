package com.practice.spring_ai.service;

import org.springframework.stereotype.Service;

@Service
public class ChatBotService {

    public String getBotResponse(String message){
        return "response from service";
    }
}
