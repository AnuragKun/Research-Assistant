package com.arlabs.researchAssistant.controller;


import com.arlabs.researchAssistant.entity.ResearchNote;
import com.arlabs.researchAssistant.exception.ResourceNotFoundException;
import com.arlabs.researchAssistant.service.ResearchNoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ResearchNoteController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ResearchNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResearchNoteService researchNoteService;

    @Autowired
    private ObjectMapper objectMapper;


    private ResearchNote sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = new ResearchNote();
        sampleNote.setId(1L);
        sampleNote.setTitle("Test Note");
        sampleNote.setContent("This is test content");
        sampleNote.setCreatedAt(LocalDateTime.now());
    }


    // --- TEST 1: POST /api/notes should return 200 and the saved note ---
    @Test
    @DisplayName("POST /api/notes - Should create a note and return 200")
    void createNote_ShouldReturn200() throws Exception {
        when(researchNoteService.saveNote(any(ResearchNote.class))).thenReturn(sampleNote);
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleNote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Note"))
                .andExpect(jsonPath("$.content").value("This is test content"));
    }


    // --- TEST 2: GET /api/notes should return a list ---
    @Test
    @DisplayName("GET /api/notes - Should return list of notes and 200")
    void getAllNotes_ShouldReturn200() throws Exception {
        when(researchNoteService.getAllNotes()).thenReturn(Arrays.asList(sampleNote));
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Note"));
    }


    // --- TEST 3: GET /api/notes/1 should return a single note ---
    @Test
    @DisplayName("GET /api/notes/1 - Should return a specific note")
    void getNoteById_ShouldReturn200() throws Exception {
        when(researchNoteService.getNoteById(1L)).thenReturn(sampleNote);
        mockMvc.perform(get("/api/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Note"));
    }


    // --- TEST 4: GET /api/notes/99 should return 404 when note not found ---
    @Test
    @DisplayName("GET /api/notes/99 - Should return 404 when note doesn't exist")
    void getNoteById_WhenNotFound_ShouldReturn404() throws Exception {
        when(researchNoteService.getNoteById(99L))
                .thenThrow(new ResourceNotFoundException("Research Note with ID: 99 was not found!"));
        mockMvc.perform(get("/api/notes/99"))
                .andExpect(status().isNotFound());
    }


    // --- TEST 5: DELETE /api/notes/1 should return 204 No Content ---
    @Test
    @DisplayName("DELETE /api/notes/1 - Should return 204 No Content")
    void deleteNote_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/notes/1"))
                .andExpect(status().isNoContent());
    }
}


