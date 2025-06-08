package org.example.projectjava.controller.departmentController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DepartmentController {
    @GetMapping("/departments")
    public String getAllDepartments() {
        return "department/department";
    }
}
