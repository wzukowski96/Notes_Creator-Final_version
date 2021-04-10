package com.notescreator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoteHistory {
    private long id;
    private int rev;
    private int revtype;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private int version;
}
