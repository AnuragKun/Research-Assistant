package com.arlabs.researchAssistant.service;


import com.arlabs.researchAssistant.entity.ResearchNote;
import com.arlabs.researchAssistant.exception.ResourceNotFoundException;
import com.arlabs.researchAssistant.repository.ResearchNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResearchNoteServiceTest {

    @Mock
    private ResearchNoteRepository researchNoteRepository;

    @InjectMocks
    private ResearchNoteService researchNoteService;

    private ResearchNote sampleNote;

    @BeforeEach
    void setUp(){
        sampleNote = new ResearchNote();
        sampleNote.setId(1L);
        sampleNote.setTitle("Test Note");
        sampleNote.setContent("This is a test content");
        sampleNote.setCreatedAt(LocalDateTime.now());
    }

    // save a note successfully
    @Test
    @DisplayName("Should save a note and return it")
    void saveNote_ShouldReturnSavedNote(){
        when(researchNoteRepository.save(any(ResearchNote.class))).thenReturn(sampleNote);

        ResearchNote result = researchNoteService.saveNote(sampleNote);

        assertNotNull(result);
        assertEquals("Test Note",result.getTitle());
        assertEquals("This is a test content",result.getContent());

        verify(researchNoteRepository,times(1)).save(any(ResearchNote.class));

    }

    //get all notes
    @Test
    @DisplayName("Should return a list of all notes")
    void findAllNotes_ShouldReturnAllNotes(){
        ResearchNote secondNote = new ResearchNote();
        secondNote.setId(2L);
        secondNote.setTitle("Second Note");
        secondNote.setContent("This is a second note");

        when(researchNoteRepository.findAll()).thenReturn(Arrays.asList(sampleNote,secondNote));
        List<ResearchNote> results = researchNoteService.getAllNotes();

        assertEquals(2,results.size());
        verify(researchNoteRepository,times(1)).findAll();

    }

    // get a note by ID (found)
    @Test
    @DisplayName("Should return a note when a valid ID is given")
    void findNoteById_ShouldReturnNote(){
        when(researchNoteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

        ResearchNote result = researchNoteService.getNoteById(1L);

        assertNotNull(result);
        assertEquals(1L,result.getId());
        assertEquals("Test Note",result.getTitle());
        assertEquals("This is a test content",result.getContent());
    }


    // get a note by id (not found)
    @Test
    @DisplayName("Should throw a ResourceNotFoundException when note ID does not exist")
    void getNoteById_ShouldThrowResourceNotFoundException(){
        when(researchNoteRepository.findById(5L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> researchNoteService.getNoteById(5L));
        assertEquals("Research Note with ID: 5 was not found!",exception.getMessage());
    }

    //delete a note
    @Test
    @DisplayName("Should call repository deleteById once")
    void deleteNoteById_ShouldDeleteNote(){
        doNothing().when(researchNoteRepository).deleteById(1L);
        researchNoteService.deleteNote(1L);
        verify(researchNoteRepository, times(1)).deleteById(1L);
    }
}
