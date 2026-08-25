package com.springai.BootAi.Controller;

import com.springai.BootAi.Service.AIAnimationService;
import com.springai.BootAi.Service.AIService;
import com.springai.BootAi.Service.Videosection;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;
    private final Videosection videoService;
    private final AIAnimationService aiAnimationService;


    public AIController(AIService aiService ,Videosection videoService,AIAnimationService aiAnimationService) {
        this.aiService = aiService;
        this.videoService=videoService;
        this.aiAnimationService=aiAnimationService;
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
    @PostMapping("/script")
    public String generateScript(
            @RequestParam String topic) {

        return aiAnimationService
                .generateScript(topic);
    }

    @PostMapping("/video")
    public Map<String, String> generateVideo(
            @RequestParam String topic) throws Exception {

        // 1. Generate animation script using Spring AI
        String script =
                aiAnimationService.generateScript(topic);

        // 2. Create video using Java + FFmpeg
        String video =
                videoService.createVideo(topic);

        return Map.of(
                "topic", topic,
                "script", script,
                "video", video
        );
    }
}
