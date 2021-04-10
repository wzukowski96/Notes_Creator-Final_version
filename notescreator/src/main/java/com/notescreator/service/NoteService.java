package com.notescreator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.notescreator.dto.NoteDTO;
import com.notescreator.model.ListOfNotes;
import com.notescreator.model.Note;
import com.notescreator.model.NoteHistory;
import com.notescreator.repository.NoteRepository;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public NoteDTO saveNote(NoteDTO noteDTO) {

        Note note = new Note(noteDTO.getTitle(), noteDTO.getContent());
        ListOfNotes listOfNotes = new ListOfNotes();
        List<Note> list = noteRepository.findAll();
        list.add(note);
        listOfNotes.setNotes(list);
        Note savedNote = noteRepository.save(note);

        return new NoteDTO(savedNote.getId(), savedNote.getTitle(), savedNote.getContent(), savedNote.getVersion());
    }

    @Transactional
    public String getNoteContent(String title){
        return noteRepository.findByTitle(title).getContent();
    }

    @Transactional
    public List<NoteHistory> getNoteHistory(String title){

        List<NoteHistory> historyList = this.jdbcTemplate.query(
                "select * from note_aud where title = ?",
                new Object[]{title},
                new RowMapper<NoteHistory>() {
                    public NoteHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
                        NoteHistory noteHistory = new NoteHistory();
                        noteHistory.setId(rs.getLong("id"));
                        noteHistory.setRev(rs.getInt("rev"));
                        noteHistory.setRevtype(rs.getInt("revtype"));
                        noteHistory.setTitle(rs.getString("title"));
                        noteHistory.setContent(rs.getString("content"));
                        noteHistory.setCreated(rs.getTimestamp("created").toLocalDateTime());
                        noteHistory.setModified(rs.getTimestamp("modified").toLocalDateTime());
                        noteHistory.setVersion(rs.getInt("version"));

                        return noteHistory;
                    }
                });
        return historyList;
    }

    @Transactional
    public List<NoteDTO> showAllNotes(){

        return noteRepository.findAllByDeletedEqualsOrderByTitleAsc(0).stream()
                .map(note -> new NoteDTO(note.getId(),
                        note.getTitle(),note.getContent(),note.getVersion(),note.getDeleted(),
                        note.getModified(),note.getModified()))
                .filter(s -> s.getDeleted() == 0).collect(Collectors.toList());
    }

    @Transactional
    public NoteDTO updateNote(NoteDTO noteDTO) {

        Note note = noteRepository.findByTitle(noteDTO.getTitle());
        ListOfNotes listOfNotes = new ListOfNotes();

        if(note != null){
            note.setContent(noteDTO.getContent());
            note.setVersion(note.getVersion()+1);
            List<Note> list = noteRepository.findAll();
            list.add(note);
            listOfNotes.setNotes(list);
            noteRepository.save(note);

            return new NoteDTO(note.getId(),note.getTitle(),note.getContent(), note.getVersion(),
                    note.getDeleted(), note.getCreated(), note.getModified());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public NoteDTO deleteNote(NoteDTO noteDTO){
        Note note = noteRepository.findByTitle(noteDTO.getTitle());
        if(note != null){
            note.setVersion(note.getVersion());
            note.setDeleted(1);
            note = noteRepository.save(note);
            return new NoteDTO(note.getId(),note.getTitle(),note.getContent(), note.getVersion(),
                    note.getDeleted(), note.getCreated(), note.getModified());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
