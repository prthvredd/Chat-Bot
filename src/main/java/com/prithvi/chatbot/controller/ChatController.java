package com.prithvi.chatbot.controller;

import com.prithvi.chatbot.dto.ChatRequest;
import com.prithvi.chatbot.dto.ChatResponse;
import com.prithvi.chatbot.service.ChatService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
        String reply = chatService.getReply(request.getMessage(), request.getLevel());
        return new ChatResponse(reply);
    }
    @PostMapping("/summarize") //apis right here baby :))
    public ChatResponse summarize(@RequestBody ChatRequest request) {
        String summary = chatService.summarize(request.getMessage(), request.getLevel());
        return new ChatResponse(summary);
    }//gonna create a ppt summarizer hehehee ::))))
    @PostMapping("/generate-ppt")
    public ResponseEntity<byte[]> generatePpt(@RequestBody ChatRequest request) throws IOException {

        byte[] pptBytes = chatService.generatePpt(request.getMessage(), request.getLevel());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "presentation.pptx");

        return ResponseEntity.ok().headers(headers).body(pptBytes);
    }

}
