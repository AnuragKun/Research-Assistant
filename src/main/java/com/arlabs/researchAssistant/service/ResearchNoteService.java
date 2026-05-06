package com.arlabs.researchAssistant.service;

import com.arlabs.researchAssistant.entity.ResearchNote;
import com.arlabs.researchAssistant.exception.ResourceNotFoundException;
import com.arlabs.researchAssistant.repository.ResearchNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResearchNoteService {
    private final ResearchNoteRepository researchNoteRepository;

    //create and update a note

    public ResearchNote saveNote(ResearchNote researchNote){
        return researchNoteRepository.save(researchNote);
    }

    //read all notes

    public List<ResearchNote> getAllNotes(){
        return researchNoteRepository.findAll();
    }

    //read a single note by id

    public ResearchNote getNoteById(Long id){
        return researchNoteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Research Note with ID: "+ id + " was not found!"));
    }

    //delete a note

    public void deleteNote(Long id){
        researchNoteRepository.deleteById(id);
    }

}
