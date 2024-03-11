package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.model.FileForm;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FileService fileService;
    private final UserService userService;
    private final NoteService noteService;
    private final CredentialService credentialService;
    private final EncryptionService encryptionService;

    public HomeController(
            FileService fileService, UserService userService, NoteService noteService,
            CredentialService credentialService, EncryptionService encryptionService) {
        this.fileService = fileService;
        this.userService = userService;
        this.noteService = noteService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    public FileService getFileService() {
        return fileService;
    }

    public UserService getUserService() {
        return userService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public CredentialService getCredentialService() {
        return credentialService;
    }

    public EncryptionService getEncryptionService() {
        return encryptionService;
    }

    //Method that handles GET requests for the home page of a web application,
    //providing data such as files, notes, and credentials of the authenticated user.
    @GetMapping
    public String getHomePage(
            Authentication authentication, @ModelAttribute("newFile") FileForm newFile,
            @ModelAttribute("newNote") NoteForm newNote, @ModelAttribute("newCredential") CredentialForm newCredential,
            Model model) {
            Integer userId = getUserId(authentication);
            model.addAttribute("files", this.fileService.getFileListings(userId));
            model.addAttribute("notes", noteService.getNoteListings(userId));
            model.addAttribute("credentials", credentialService.getCredentialListings(userId));
            model.addAttribute("encryptionService", encryptionService);

            return "home";
    }
    //This method retrieves the user ID of the authenticated user by querying
    //the user service with the username obtained from the authentication object.
    private Integer getUserId(Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        return user.getUserId();
    }

    //This method handles a POST request to add a new file in the web application,
    //checking if the file already exists for the current user.
    @PostMapping
    public String newFile(
            Authentication authentication, @ModelAttribute("newFile") FileForm newFile,
            @ModelAttribute("newNote") NoteForm newNote, @ModelAttribute("newCredential") CredentialForm newCredential, Model model) throws IOException {
            String userName = authentication.getName();
            User user = userService.getUser(userName);
            Integer userId = user.getUserId();
            String[] fileListings = fileService.getFileListings(userId);
            MultipartFile multipartFile = newFile.getFile();
            String fileName = multipartFile.getOriginalFilename();
            boolean fileIsDuplicate = false;
            for (String fileListing: fileListings) {
                if (fileListing.equals(fileName)) {
                    fileIsDuplicate = true;

                    break;
                }
            }
            if (!fileIsDuplicate) {
                fileService.addFile(multipartFile, userName);
                model.addAttribute("result", "success");
            } else {
                model.addAttribute("result", "error");
                model.addAttribute("message", "You have tried to add a duplicate file.");
            }
            model.addAttribute("files", fileService.getFileListings(userId));

            return "result";
    }

    //This method handles a GET request to retrieve a specific file in the web application,
    //returning the requested file's data.
    @GetMapping(
        value = "/get-file/{fileName}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody
    byte[] getFile(@PathVariable String fileName) {
        return fileService.getFile(fileName).getFileData();
    }

    //This method handles a GET request to delete a specific file in the web application,
    //removing it from the system and returning a success message.
    @GetMapping(value = "/delete-file/{fileName}")
    public String deleteFile(
        Authentication authentication, @PathVariable String fileName, @ModelAttribute("newFile") FileForm newFile,
        @ModelAttribute("newNote") NoteForm newNote, @ModelAttribute("newCredential") CredentialForm newCredential,
        Model model) {
            fileService.deleteFile(fileName);
            Integer userId = getUserId(authentication);
            model.addAttribute("files", fileService.getFileListings(userId));
            model.addAttribute("result", "success");

        return "result";
    }
}
