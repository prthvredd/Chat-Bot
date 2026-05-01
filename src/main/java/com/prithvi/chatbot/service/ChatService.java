package com.prithvi.chatbot.service;

import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final GrokService grokService;
    public ChatService(GrokService grokService){
        this.grokService = grokService; //connect the grokservice to chatservice
    }
    public String getReply(String message){
        String prompt = "You are a SmartNotes AI assistant. " +
                "Summarize or explain clearly in simple terms:\n\n"
                + message;
        return grokService.callGrok(prompt);
    }
    public String summarize(String text) {
        String prompt = "Summarize this following text in a easy understable way" + text;
        return grokService.callGrok(prompt);

    }

}
