package org.example.projectjava.Controller.MembersController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AllMembersController {
    @GetMapping("/all-members")
    public String showDepartmentMembers() {
        return "allMembers/allMembers";
    }
}
