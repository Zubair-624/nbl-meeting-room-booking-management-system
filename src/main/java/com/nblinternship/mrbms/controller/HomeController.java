package com.nblinternship.mrbms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // If someone asks for “/” (home page), send them to the home() method.
    //@GetMapping("/") does not do any work.
    //It only tells Spring: Which method should run?
    @GetMapping("/")
    public String home(){
        return "home"; //Show the home.html page
    }


}
