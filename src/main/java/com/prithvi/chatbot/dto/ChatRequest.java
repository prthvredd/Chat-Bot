package com.prithvi.chatbot.dto;
import lombok.Data;
@Data

public class ChatRequest {
    private String message;
    private String level;

    public String getMessage() {
        return message;
    }
    public String getLevel() {
        return level;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setLevel(String level){
        this.level = level;
    }
}
