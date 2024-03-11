package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/signup")
public class SignupController {
    private final UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    //This method handles a GET request to render the signup view page in the web application,
    // returning the "signup" page.
    @GetMapping()
    public String signupView() {
        return "signup";
    }

    //This method handles a POST request to sign up a new user in the web application.
    //It checks if the provided username is available and if not, sets an error message.
    //If no error occurs, it attempts to create the user and sets a success message if successful,
    //otherwise sets an error message. Finally, it returns the "signup" page.
    @PostMapping()
    public String signupUser(@ModelAttribute User user, Model model) {
        String signupError = null;

        if (!userService.isUsernameAvailable(user.getUsername())) {
            signupError = "The username already exists.";
        }

        if (signupError == null) {
            int rowsAdded = userService.createUser(user);
            if (rowsAdded < 0) {
                signupError = "There was an error signing you up. Please try again.";
            }
        }

        if (signupError == null) {
            model.addAttribute("signupSuccess", true);
        } else {
            model.addAttribute("signupError", signupError);
        }
        return "signup";
    }
}
