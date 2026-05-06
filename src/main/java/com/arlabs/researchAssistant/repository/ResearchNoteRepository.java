package com.arlabs.researchAssistant.repository;

import com.arlabs.researchAssistant.entity.ResearchNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchNoteRepository extends JpaRepository<ResearchNote,Long> {
}
