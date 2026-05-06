package com.arlabs.researchAssistant.controller;

import com.arlabs.researchAssistant.entity.ResearchNote;
import com.arlabs.researchAssistant.service.ResearchNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
@Tag(name = "Research Notes API", description = "Endpoints for creating, reading, updating, and deleting saved research notes")
public class ResearchNoteController {

    private final ResearchNoteService researchNoteService;
    @Operation(summary = "Create a new note", description = "Saves a new research note to the database")
    @PostMapping
    public ResponseEntity<ResearchNote> createNote(@Valid @RequestBody ResearchNote researchNote){
        ResearchNote savedNote = researchNoteService.saveNote(researchNote);
        return ResponseEntity.ok(savedNote);
    }

    @Operation(summary = "Get all notes", description = "Retrieves a list of all saved research notes")
    @GetMapping
    public ResponseEntity<List<ResearchNote>> getAllNotes(){
        return ResponseEntity.ok(researchNoteService.getAllNotes());
    }

    @Operation(summary = "Get a specific note", description = "Retrieves a single research note by its database ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResearchNote> getNoteById(@PathVariable Long id){
        ResearchNote note = researchNoteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    @Operation(summary = "Update an existing note", description = "Updates the title and content of a specific note using its ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResearchNote> updateNote(@Valid @PathVariable Long id, @RequestBody ResearchNote noteDetails){
        ResearchNote existingNote = researchNoteService.getNoteById(id);
        existingNote.setContent(noteDetails.getContent());
        existingNote.setTitle(noteDetails.getTitle());
        ResearchNote updatedNote = researchNoteService.saveNote(existingNote);
        return ResponseEntity.ok(updatedNote);
    }

    @Operation(summary = "Delete a note", description = "Permanently removes a research note from the database")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id){
        researchNoteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}