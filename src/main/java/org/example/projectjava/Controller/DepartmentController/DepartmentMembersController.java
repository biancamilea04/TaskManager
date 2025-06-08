package org.example.projectjava.Controller.DepartmentController;

import org.example.projectjava.ControllerDTO.MembersDTO.MemberDepartmentDTO;
import org.example.projectjava.Model.Department.Department;
import org.example.projectjava.Model.Member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DepartmentMembersController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/department-members/{departmentName}")
    public String showDepartmentMembers() {
        return "departmentMembers/departmentMembers";
    }

    @GetMapping("/api/department/members/{departmentNameParam}")
    public ResponseEntity<?> getMembersByDepartment(@PathVariable String departmentNameParam) {
        String departmentName = Department.departmentNames.get(departmentNameParam);
        List<MemberDepartmentDTO> members = memberService.getMembersByDepartment(departmentName);

        return ResponseEntity.ok(members);
    }
}
