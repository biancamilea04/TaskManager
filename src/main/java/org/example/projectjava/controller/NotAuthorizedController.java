package org.example.projectjava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotAuthorizedController {

    @GetMapping("/not-authorized")
    public String notAuthorizedPage() {
        return "NotAuthorizedPage/NotAuthorizedPage";
    }
}
