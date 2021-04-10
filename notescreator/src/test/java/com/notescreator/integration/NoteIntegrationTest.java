package com.notescreator.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notescreator.dto.NoteDTO;
import com.notescreator.model.Note;
import com.notescreator.repository.NoteRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import static io.restassured.RestAssured.with;
import static org.junit.jupiter.api.Assertions.fail;

@AutoConfigureMockMvc
class NoteIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void before() {
        RestAssured.port = port;
    }

    @BeforeAll
    public static void init() {
        RestAssured.baseURI = "http://localhost";
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NoteRepository noteRepository;

    @Test
    void shouldCreateNewNoteWithTitleAndContent() {

        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setTitle("Hello");
        noteDTO.setContent("world");

        Response response = with().body(noteDTO)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/notes/create");

        Assertions.assertEquals(200, response.statusCode());

        try {
            NoteDTO result = response.body().as(NoteDTO.class);
            Assertions.assertEquals(noteDTO.getTitle(), result.getTitle());
            Assertions.assertEquals(noteDTO.getContent(), result.getContent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void shouldUpdateTheNoteWithDifferentContent() {

        Note noteInDb = new Note();
        noteInDb.setTitle("my");
        noteInDb.setContent("car");
        noteRepository.save(noteInDb);

        NoteDTO updatedNoteDTO = new NoteDTO();
        updatedNoteDTO.setTitle("my");
        updatedNoteDTO.setContent("house");

        Response response = with().body(updatedNoteDTO)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/notes/update");

        Assertions.assertEquals(200, response.statusCode());

        try {
            NoteDTO result = response.body().as(NoteDTO.class);
            Assertions.assertNotEquals(noteInDb.getContent(), result.getContent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void shouldDeleteTheNoteAndChangeDeletedVariable() {

        Note noteInDb = new Note();
        noteInDb.setTitle("my");
        noteInDb.setContent("car");
        noteRepository.save(noteInDb);

        Response response = with().body(noteInDb)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/notes/delete");

        Assertions.assertEquals(200, response.statusCode());

        try {
            NoteDTO result = response.body().as(NoteDTO.class);
            Assertions.assertNotEquals(noteInDb.getDeleted(), result.getDeleted());
        } catch (Exception e) {
            fail(e);
        }
    }
}
