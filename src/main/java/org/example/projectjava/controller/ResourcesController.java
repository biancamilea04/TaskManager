package org.example.projectjava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResourcesController {
    @GetMapping("/resources")
    public String getResources() {
        return "resurse/resurse";
    }
}
