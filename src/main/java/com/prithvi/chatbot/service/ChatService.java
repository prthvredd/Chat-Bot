package com.prithvi.chatbot.service;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xslf.usermodel.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.IOException;

@Service
public class ChatService {
    private final GrokService grokService;
    public ChatService(GrokService grokService){
        this.grokService = grokService; //connect the grokservice to chatservice
    }
    public String getReply(String message, String level){
        if(message == null || message.isBlank()) {
            throw new  IllegalArgumentException("The field cannot be blank");
        }
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
    public byte[] generatePpt(String message, String level) throws IOException {

        // 1. Build prompt
        String prompt = "You are a SmartNotes AI assistant. " +
                "Create a presentation at a " + normalizeLevel(level) + " level. " +
                "Respond ONLY in this exact JSON format, no extra text:\n" +
                "{\"title\":\"...\",\"slides\":[{\"heading\":\"...\",\"bullets\":[\"...\",\"...\"]}]}\n\n" +
                "Topic: " + message;

        // 2. Call Grok
        String aiResponse = grokService.callGrok(prompt);

        // 3. Parse JSON  ← this must be INSIDE the method
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json;
        try {
            json = mapper.readTree(aiResponse);
        } catch (Exception e) {
            throw new RuntimeException("AI returned invalid JSON: " + aiResponse);
        }

        // 4. Build PPTX  ← this must also be INSIDE the method
        try (XMLSlideShow ppt = new XMLSlideShow();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSLFSlideMaster master = ppt.getSlideMasters().get(0);
            XSLFSlideLayout titleLayout = null;
            XSLFSlideLayout contentLayout = null;

            for (XSLFSlideLayout layout : master.getSlideLayouts()) {
                String name = layout.getName().toLowerCase();
                if (titleLayout == null && name.contains("title")) {
                    titleLayout = layout;
                }
                if (contentLayout == null && name.contains("content")) {
                    contentLayout = layout;
                }
            }

            if (titleLayout == null)   titleLayout   = master.getSlideLayouts()[0];
            if (contentLayout == null) contentLayout = master.getSlideLayouts()[1];

            // Title slide
            XSLFSlide titleSlide = ppt.createSlide(titleLayout);
            if (titleSlide.getPlaceholders().length > 0) {
                titleSlide.getPlaceholders()[0].setText(json.get("title").asText());
            }

            // Content slides
            for (JsonNode slide : json.get("slides")) {
                XSLFSlide s = ppt.createSlide(contentLayout);
                XSLFTextShape[] placeholders = s.getPlaceholders();

                if (placeholders.length > 0)
                    placeholders[0].setText(slide.get("heading").asText());

                if (placeholders.length > 1) {
                    XSLFTextShape body = placeholders[1];
                    body.clearText();
                    for (JsonNode bullet : slide.get("bullets")) {
                        XSLFTextParagraph para = body.addNewTextParagraph();
                        para.addNewTextRun().setText("• " + bullet.asText());
                    }
                }
            }

            ppt.write(out);
            return out.toByteArray();

        } // ← method closes here
    }
    }


