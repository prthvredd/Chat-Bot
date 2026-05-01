package com.prithvi.chatbot.controller;

import com.prithvi.chatbot.dto.ChatRequest;
import com.prithvi.chatbot.dto.ChatResponse;
import com.prithvi.chatbot.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService){
        this.chatService  = chatService;
    }
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatService.getReply(request.getMessage());
        return new ChatResponse(reply);
    }
    @PostMapping("/summarize") //apis right here baby :))
    public ChatResponse summarize(@RequestBody ChatRequest request) {
        String summary = chatService.summarize(request.getMessage());
        return new ChatResponse(summary);
    }

}
