package com.arlabs.researchAssistant.controller;

import com.arlabs.researchAssistant.ResearchRequest;
import com.arlabs.researchAssistant.service.ResearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
@Tag(name = "Research Assistant API", description = "EndPoints for interacting with the Gemini AI model")
public class ResearchController {

    private final ResearchService researchService;

    @Operation(summary = "Process Content", description = "Sends research text to Gemini for summarization or suggestions")
    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchRequest researchRequest){
        String result = researchService.processContent(researchRequest);
        return ResponseEntity.ok(result);
    }
}
