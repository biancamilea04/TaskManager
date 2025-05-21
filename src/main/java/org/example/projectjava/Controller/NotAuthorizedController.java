package org.example.projectjava.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotAuthorizedController {

    @GetMapping("/NotAuthorizedPage")
    public String notAuthorizedPage() {
        System.out.println("Not authorized page accessed");
        return "NotAuthorizedPage/NotAuthorizedPage";
    }
}
