package com.springai.BootAi.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.util.List;

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

        public String askPdf(String question) {

            // Read PDF
            FileSystemResource resource =
                    new FileSystemResource("MODULES.pdf");

            PagePdfDocumentReader reader =
                    new PagePdfDocumentReader(resource);

            List<Document> documents = reader.get();

            // Convert PDF content to text
            String pdfText = documents.stream()
                    .map(Document::getText)
                    .reduce("", (a, b) -> a + "\n" + b);

            // Send PDF content + question to AI
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
    }

