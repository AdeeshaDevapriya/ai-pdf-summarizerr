package com.example.pdfsummarizer.controller;

import com.example.pdfsummarizer.service.GeminiApiService;
import com.example.pdfsummarizer.service.PdfExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
@Slf4j
public class PdfSummarizerController {

    private final PdfExtractionService pdfExtractionService;
    private final GeminiApiService geminiApiService;

    @PostMapping(value = "/summarize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> summarizePdf(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            if (!file.getContentType().equals("application/pdf")) {
                response.put("error", "File must be a PDF");
                return ResponseEntity.badRequest().body(response);
            }

            log.info("Processing PDF file: {}", file.getOriginalFilename());
            
            // Extract text from PDF
            String extractedText = pdfExtractionService.extractTextFromPdf(file);
            
            if (extractedText == null || extractedText.trim().isEmpty()) {
                response.put("error", "No text could be extracted from the PDF");
                return ResponseEntity.badRequest().body(response);
            }

            // Summarize text using Gemini API
            String summary = geminiApiService.summarizeText(extractedText);
            
            response.put("summary", summary);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Error processing PDF file", e);
            response.put("error", "Error processing PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

