// package com.example.pdfsummarizer.service;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class GeminiApiService {

//     private final RestTemplate restTemplate;
//     private final ObjectMapper objectMapper;

//     @Value("${google.gemini.api.key}")
//     private String apiKey;

//     @Value("${google.gemini.api.url}")
//     private String apiUrl;

//     public String summarizeText(String text) {
//         try {
//             // Validate text is not empty
//             if (text == null || text.trim().isEmpty()) {
//                 log.error("Text content is empty, cannot summarize");
//                 return "Error: Text content is empty";
//             }
            
//             // Build URL exactly as specified: base URL + ?key= + apiKey
//             String url = apiUrl + "?key=" + apiKey;
//             log.info("Gemini API URL: {}", url.replace(apiKey, "***"));
//             log.debug("Text content length: {} characters", text.length());
            
//             // Construct request body with exact structure for Gemini API v1beta
//             Map<String, Object> requestBody = new HashMap<>();
//             Map<String, Object> content = new HashMap<>();
//             Map<String, Object> part = new HashMap<>();
//             part.put("text", "Summarize this text: " + text);
            
//             content.put("parts", List.of(part));
//             requestBody.put("contents", List.of(content));

//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_JSON);

//             HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

//             log.info("Calling Gemini API for text summarization");
//             try {
//                 log.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));
//             } catch (Exception e) {
//                 log.debug("Could not serialize request body for logging");
//             }
            
//             ResponseEntity<String> response = restTemplate.exchange(
//                     url,
//                     HttpMethod.POST,
//                     request,
//                     String.class
//             );

//             if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                 JsonNode jsonNode = objectMapper.readTree(response.getBody());
//                 JsonNode candidates = jsonNode.get("candidates");
//                 if (candidates != null && candidates.isArray() && candidates.size() > 0) {
//                     JsonNode contentNode = candidates.get(0).get("content");
//                     if (contentNode != null) {
//                         JsonNode parts = contentNode.get("parts");
//                         if (parts != null && parts.isArray() && parts.size() > 0) {
//                             JsonNode textNode = parts.get(0).get("text");
//                             if (textNode != null) {
//                                 String summary = textNode.asText();
//                                 log.info("Successfully received summary from Gemini API");
//                                 return summary;
//                             }
//                         }
//                     }
//                 }
//                 log.warn("Unexpected response structure from Gemini API");
//                 return "Error: Unable to parse response from Gemini API";
//             } else {
//                 log.error("Gemini API returned error: {} - Response body: {}", 
//                     response.getStatusCode(), response.getBody());
//                 return "Error: Failed to get summary from Gemini API - " + response.getStatusCode();
//             }
//         } catch (org.springframework.web.client.ResourceAccessException e) {
//             log.error("Connection error calling Gemini API (timeout or connection aborted): {}", e.getMessage());
//             if (e.getCause() != null) {
//                 log.error("Root cause: {}", e.getCause().getMessage());
//             }
//             return "Error: Connection timeout or aborted. The request may be too large or the API is slow. Please try again.";
//         } catch (org.springframework.web.client.HttpClientErrorException e) {
//             log.error("HTTP error calling Gemini API: {} - Response: {}", 
//                 e.getStatusCode(), e.getResponseBodyAsString());
//             return "Error: " + e.getResponseBodyAsString();
//         } catch (org.springframework.web.client.HttpServerErrorException e) {
//             log.error("Server error calling Gemini API: {} - Response: {}", 
//                 e.getStatusCode(), e.getResponseBodyAsString());
//             return "Error: Gemini API server error - " + e.getStatusCode();
//         } catch (Exception e) {
//             log.error("Error calling Gemini API", e);
//             return "Error: " + e.getMessage();
//         }
//     }
// }



package com.example.pdfsummarizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    public String summarizeText(String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                log.error("Input text is empty.");
                return "Error: Text content is empty";
            }

            // SAFETY FIX: Trimming the text to stay within Groq Free Tier TPM limits
            // 30,000 characters is roughly 7,000-8,000 tokens.
            String truncatedText = text;
            if (text.length() > 30000) {
                log.warn("Text length ({}) exceeds safe limits. Truncating to 30,000 characters.", text.length());
                truncatedText = text.substring(0, 30000);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "You are a professional assistant. Summarize the text concisely."));
            messages.add(Map.of("role", "user", "content", "Summarize this document: " + truncatedText));
            
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to Groq API (Text length: {})...", truncatedText.length());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String summary = jsonNode.path("choices").get(0).path("message").path("content").asText();
                log.info("Successfully retrieved summary from Groq API");
                return summary;
            } else {
                return "Error: Groq API error - " + response.getStatusCode();
            }

        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            log.error("Rate limit exceeded on Groq API: {}", e.getResponseBodyAsString());
            return "Error: API rate limit exceeded. Please wait a minute or use a smaller PDF.";
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            return "Error: " + e.getMessage();
        }
    }
}