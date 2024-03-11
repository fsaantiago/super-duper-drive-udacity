package com.udacity.jwdnd.course1.cloudstorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/result")
public class ResultController {

    //This method handles a GET request to render the result view page in the web application,
    // returning the "result" page.
    @GetMapping()
    public String resultView() {
        return "result";
    }
}
