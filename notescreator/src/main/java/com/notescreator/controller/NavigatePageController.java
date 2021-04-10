package com.notescreator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.notescreator.service.NoteService;

@Controller
public class NavigatePageController {

    private final NoteService noteService;

    @Autowired
    public NavigatePageController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/")
    public String showAllNotes(Model model){
        model.addAttribute("notes",noteService.showAllNotes());
        return "main_page";
    }
}
