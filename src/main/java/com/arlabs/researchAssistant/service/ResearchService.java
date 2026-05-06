package com.arlabs.researchAssistant.service;

import com.arlabs.researchAssistant.dto.GeminiResponse;
import com.arlabs.researchAssistant.ResearchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.Map;

@Service
public class ResearchService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    public ResearchService(RestClient.Builder restClientBuilder, ObjectMapper objectMapper){
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
    }



    public String processContent(ResearchRequest researchRequest) {
        //Build the prompt
        String prompt = buildPrompt(researchRequest);
        //query the ai model api
        Map<String, Object> requestBody = Map.of(
          "contents", new Object[] {
                  Map.of("parts", new Object[] {
                          Map.of("text", prompt)
                  })
                }
        );

        String response = restClient
                .post()
                .uri(geminiApiUrl+geminiApiKey)
                .body(requestBody)
                .retrieve()
                .body(String.class);
        //parse the response
        //return response

        return extractTextFromResponse( response);
    }

    private String extractTextFromResponse(String response) {
        try{
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
            if(geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()){
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if(firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null && !firstCandidate.getContent().getParts().isEmpty()){
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }
            return "No content found in response";
        } catch (Exception e){
            return "Error Parsing: "+e.getMessage();
        }
    }

    private String buildPrompt(ResearchRequest researchRequest){
        StringBuilder prompt = new StringBuilder();
        switch (researchRequest.getOperation()){
            case "summarize":
                prompt.append("Provide a clear and concise summary of the following text in a few bullet points, also instead of using '#', '*' for points and subpoints use digits and/or roman numricals: \n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content: suggest related topics and further reading. Format the response with clear headings and bullet points:\n\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown Operation: " + researchRequest.getOperation());
        }
        prompt.append(researchRequest.getContent());
        return prompt.toString();
    }
}
