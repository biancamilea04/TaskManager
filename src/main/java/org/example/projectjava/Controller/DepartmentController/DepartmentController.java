package org.example.projectjava.Controller.DepartmentController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DepartmentController {
    @GetMapping("/departments")
    public String getAllDepartments() {
        return "department/department";
    }
}
