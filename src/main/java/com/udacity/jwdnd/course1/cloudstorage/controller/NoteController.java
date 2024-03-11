package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("note")
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public UserService getUserService() {
        return userService;
    }

    //This method handles a GET request for the home page of the web application,
    //adding notes belonging to the authenticated user to the page's model and returning the "home" page.
    @GetMapping
    public String getHomePage(
            Authentication authentication, @ModelAttribute("newFile") FileForm newFile, @ModelAttribute("newNote") NoteForm newNote,
            @ModelAttribute("newCredential") CredentialForm newCredential, Model model) {
        Integer userId = getUserId(authentication);
        model.addAttribute("notes", this.noteService.getNoteListings(userId));

        return "home";
    }

    //This method retrieves the user ID of the authenticated user by querying
    //the user service with the username obtained from the authentication object.
    private Integer getUserId(Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        return user.getUserId();
    }

    //This method handles a POST request to add or update a note in the web application.
    //It extracts information such as the note title and description from the provided NoteForm object,
    //and then either adds a new note or updates an existing one based on the presence of a note ID.
    //Finally, it adds the updated list of notes to the model and returns a success message.
    @PostMapping("add-note")
    public String newNote(
            Authentication authentication, @ModelAttribute("newFile") FileForm newFile,
            @ModelAttribute("newNote") NoteForm newNote, @ModelAttribute("newCredential") CredentialForm newCredential,
            Model model) {
        String userName = authentication.getName();
        String newTitle = newNote.getTitle();
        String noteIdStr = newNote.getNoteId();
        String newDescription = newNote.getDescription();
        if (noteIdStr.isEmpty()) {
            noteService.addNote(newTitle, newDescription, userName);
        } else {
            Note existingNote = getNote(Integer.parseInt(noteIdStr));
            noteService.updateNote(existingNote.getNoteId(), newTitle, newDescription);
        }
        Integer userId = getUserId(authentication);
        model.addAttribute("notes", noteService.getNoteListings(userId));
        model.addAttribute("result", "success");

        return "result";
    }

    //This method handles a GET request to retrieve information about a specific note in the web application,
    //returning the corresponding note object for the provided ID.
    @GetMapping(value = "/get-note/{noteId}")
    public Note getNote(@PathVariable Integer noteId) {
        return noteService.getNote(noteId);
    }

    //This method handles a GET request to delete a specific note in the web application,
    //removing it from the system and returning a success message.
    @GetMapping(value = "/delete-note/{noteId}")
    public String deleteNote(
            Authentication authentication, @PathVariable Integer noteId, @ModelAttribute("newNote") NoteForm newNote,
            @ModelAttribute("newFile") FileForm newFile, @ModelAttribute("newCredential") CredentialForm newCredential,
            Model model) {
        noteService.deleteNote(noteId);
        Integer userId = getUserId(authentication);
        model.addAttribute("notes", noteService.getNoteListings(userId));
        model.addAttribute("result", "success");

        return "result";
    }
}
