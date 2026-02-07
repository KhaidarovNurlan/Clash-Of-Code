package com.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodeRunnerService {

    @Value("${onecompiler.api.url}")
    private String apiUrl;

    @Value("${onecompiler.api.key}")
    private String apiKey;

    @Value("${onecompiler.api.host}")
    private String apiHost;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, String> LANGUAGE_MAP = Map.ofEntries(
            Map.entry("python", "python"),
            Map.entry("csharp", "csharp"),
            Map.entry("java", "java"),
            Map.entry("javascript", "javascript")
    );

    public String runCode(String language, String code) {
        try {
            String lang = LANGUAGE_MAP.get(language.toLowerCase());
            if (lang == null)
                throw new RuntimeException("Unsupported language: " + language);

            if (code == null || code.trim().isEmpty())
                throw new RuntimeException("Source code cannot be empty");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("language", lang);
            requestBody.put("stdin", "");
            requestBody.put("files", List.of(Map.of(
                    "name", getMainFileName(lang),
                    "content", code
            )));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-rapidapi-key", apiKey);
            headers.set("x-rapidapi-host", apiHost);

            HttpEntity<String> requestEntity =
                    new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();

            String stdout = (String) body.getOrDefault("stdout", "");
            String stderr = (String) body.getOrDefault("stderr", "");

            if (stderr != null && !stderr.isEmpty())
                return "Error:\n" + stderr;

            if (stdout != null) {
                stdout = stdout
                        .replace("<EOL>", "")
                        .replaceAll("[\\r\\n]+$", "")
                        .trim();
            }

            return stdout.isEmpty() ? "Program executed with no output" : stdout;
        } catch (Exception e) {
            e.printStackTrace();
            return "Execution error: " + e.getMessage();
        }
    }

    private String getMainFileName(String language) {
        return switch (language) {
            case "python" -> "index.py";
            case "csharp" -> "Main.cs";
            case "java" -> "Main.java";
            case "javascript" -> "index.js";
            default -> "main.txt";
        };
    }
}
