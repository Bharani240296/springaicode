package com.springai.BootAi.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final Map<String, List<String>> conversations =
            new HashMap<>();
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

        public String askPdf(String question) {

            FileSystemResource resource =
                    new FileSystemResource("MODULES.pdf");

            PagePdfDocumentReader reader =
                    new PagePdfDocumentReader(resource);

            List<Document> documents = reader.get();

            String pdfText = documents.stream()
                    .map(Document::getText)
                    .reduce("", (a, b) -> a + "\n" + b);

            String prompt = """
                Answer the question using ONLY the PDF content below.

                PDF CONTENT:
                %s

                QUESTION:
                %s
                """.formatted(pdfText, question);

            return chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();
        }
    public String chat(String conversationId, String message) {
        System.out.println("chat history   "+conversations);
        conversations
                .computeIfAbsent(conversationId, id -> new ArrayList<>())
                .add("User: " + message);

        String history = String.join("\n",
                conversations.get(conversationId));

        String prompt = """
                You are a helpful AI assistant.

                Conversation history:
                %s

                Current user message:
                %s

                Answer the user naturally.
                """.formatted(history, message);

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        conversations
                .get(conversationId)
                .add("Assistant: " + response);

        return response;
    }
    }

