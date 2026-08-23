package com.springai.BootAi.Controller;

import com.springai.BootAi.Service.AIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;


    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String message) {
        return aiService.ask(message);
    }
    @GetMapping("/pdf")
    public String askPdf(@RequestParam String question) {

        return aiService.askPdf(question);
    }
    @PostMapping
    public String chat(
            @RequestParam String conversationId,
            @RequestParam String message) {

        return aiService.chat(conversationId, message);
    }
}
