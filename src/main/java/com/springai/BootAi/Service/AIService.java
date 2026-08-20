package com.springai.BootAi.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String ask(String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}
