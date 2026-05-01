package com.prithvi.chatbot.service;

import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final GrokService grokService;
    public ChatService(GrokService grokService){
        this.grokService = grokService; //connect the grokservice to chatservice
    }
    public String getReply(String message, String level){
        String prompt = "You are a SmartNotes AI assistant. " +
                "Explain at a " + normalizeLevel(level) + " level. " +
                "Summarize or explain clearly in simple terms:\n\n"
                + message;
        return grokService.callGrok(prompt);
    }
    public String summarize(String text, String level) {
        String prompt = "Summarize the following text at a " + normalizeLevel(level) +
                " level. Keep it clear and easy to understand:\n\n" + text;
        return grokService.callGrok(prompt);

    }

    private String normalizeLevel(String level) {
        if (level == null) {
            return "beginner";
        }

        return switch (level.toLowerCase()) {
            case "intermediate" -> "intermediate";
            case "advanced" -> "advanced";
            default -> "beginner";
        };
    }
}
