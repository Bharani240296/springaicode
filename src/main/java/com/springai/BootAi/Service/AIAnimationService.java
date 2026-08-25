package com.springai.BootAi.Service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIAnimationService {

    private final ChatClient chatClient;

    public AIAnimationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateScript(String topic) {

        String prompt = """
                You are a professional technical animation script writer.

                Create a short animation script about:

                %s

                The animation should contain exactly 5 scenes.

                For each scene provide:

                Scene:
                Duration:
                Title:
                Narration:
                Visual:

                Keep the explanation simple and professional.

                The topic is:
                %s
                """.formatted(topic, topic);

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}
