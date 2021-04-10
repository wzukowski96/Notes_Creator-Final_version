package com.notescreator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import com.notescreator.model.Note;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long>, RevisionRepository<Note,Long,Integer> {
    Note findByTitle(String title);
    List<Note> findAllByDeletedEqualsOrderByTitleAsc(int deleteNumber);
    List<Note> findAllByDeletedEqualsOrderByCreatedAsc(int deleteNumber);
    List<Note> findAllByDeletedEqualsOrderByModifiedAsc(int deleteNumber);

    @Query(value = "select n from Note n where n.title = :title")
    List<Note> findByTitleOrderByCreatedAsc(String title);
}
