package com.springai.BootAi.Controller;

import com.springai.BootAi.Service.AIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
