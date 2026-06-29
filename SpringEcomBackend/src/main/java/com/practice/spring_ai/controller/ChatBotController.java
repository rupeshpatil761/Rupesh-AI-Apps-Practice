package com.practice.spring_ai.controller;

import com.practice.spring_ai.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @GetMapping("/ask")
    public ResponseEntity<String> askBot(@RequestParam String userQuery) {
        String response = chatBotService.getBotResponse(userQuery);
        return ResponseEntity.ok(response);
    }
}
