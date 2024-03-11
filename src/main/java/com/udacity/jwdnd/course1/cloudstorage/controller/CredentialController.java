package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.security.SecureRandom;
import java.util.Base64;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("credential")
public class CredentialController {

    private final CredentialService credentialService;
    private final EncryptionService encryptionService;
    private final UserService userService;

    public CredentialController(CredentialService credentialService, EncryptionService encryptionService, UserService userService) {
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
        this.userService = userService;
    }

    public CredentialService getCredentialService() {
        return credentialService;
    }

    public EncryptionService getEncryptionService() {
        return encryptionService;
    }

    public UserService getUserService() {
        return userService;
    }

    //This method handles a GET request for the home page of the web application,
    // adding credentials belonging to the authenticated user to the page's model and returning the "home" page.
    @GetMapping
    public String getHomePage(
            Authentication authentication, @ModelAttribute("newFile") FileForm newFile,
            @ModelAttribute("newCredential") CredentialForm newCredential,
            @ModelAttribute("newNote") NoteForm newNote, Model model) {
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        model.addAttribute("credentials", this.credentialService.getCredentialListings(user.getUserId()));
        model.addAttribute("encryptionService", encryptionService);

        return "home";
    }

    //This method handles a POST request to add or update access credentials in the web application.
    //It generates a random encryption key, encodes the provided password using this key,
    //and then stores or updates the credentials in the database, returning a success message.
    @PostMapping("add-credential-access")
    public String newCredential(
            Authentication authentication, @ModelAttribute("newFile") FileForm newFile,
            @ModelAttribute("newCredentialAccess") CredentialForm newCredential,
            @ModelAttribute("newNote") NoteForm newNote, Model model) {
        String userName = authentication.getName();
        String newUrl = newCredential.getUrl();
        String credentialIdStr = newCredential.getCredentialId();
        String password = newCredential.getPassword();

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(password, encodedKey);

        if (credentialIdStr.isEmpty()) {
            credentialService.addCredential(newUrl, userName, newCredential.getUsername(), encodedKey, encryptedPassword);
        } else {
            Credential existingCredential = getCredential(Integer.parseInt(credentialIdStr));
            credentialService.updateCredential(existingCredential.getInformId(), newCredential.getUsername(), newUrl, encodedKey, encryptedPassword);
        }
        User user = userService.getUser(userName);
        model.addAttribute("credentials", credentialService.getCredentialListings(user.getUserId()));
        model.addAttribute("encryptionService", encryptionService);
        model.addAttribute("result", "success");

        return "result";
    }

    //This method handles a GET request to retrieve information about a specific credential in the web application,
    // returning the corresponding credential object for the provided ID.
    @GetMapping(value = "/get-credential/{credentialId}")
    public Credential getCredential(@PathVariable Integer credentialId) {
        return credentialService.getCredential(credentialId);
    }

    //This method handles a GET request to delete a specific credential in the web application,
    //removing it from the system and returning a success message.
    @GetMapping(value = "/delete-credential/{credentialId}")
    public String deleteCredential(
            Authentication authentication, @PathVariable Integer credentialId,
            @ModelAttribute("newCredential") CredentialForm newCredential,
            @ModelAttribute("newFile") FileForm newFile,
            @ModelAttribute("newNote") NoteForm newNote, Model model) {
        credentialService.deleteCredential(credentialId);
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        model.addAttribute("credentials", credentialService.getCredentialListings(user.getUserId()));
        model.addAttribute("encryptionService", encryptionService);
        model.addAttribute("result", "success");

        return "result";
    }
}
