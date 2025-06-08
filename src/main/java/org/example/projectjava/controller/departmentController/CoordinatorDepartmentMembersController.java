package org.example.projectjava.controller.departmentController;

import org.example.projectjava.DTO.DepartmentDTO.AddMemberToDepartmentDTO;
import org.example.projectjava.DTO.DepartmentDTO.AddedMemberToDepartmentDTO;
import org.example.projectjava.model.Department;
import org.example.projectjava.service.DepartmentService;
import org.example.projectjava.service.DepartmentMembersService;
import org.example.projectjava.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
public class CoordinatorDepartmentMembersController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private DepartmentMembersService departmentMembersService;
    @Autowired
    private DepartmentService departmentService;

    private String department;

    @PreAuthorize("hasAuthority('COORDONATOR')")
    @GetMapping("/department-members/coordinator/{departmentName}")
    public String showDepartmentMembersAsCoordinator(@PathVariable String departmentName) {
        department = Department.departmentNames.get(departmentName);
        return "departmentMembers/DepartmentMembersCoordinator";
    }

    @GetMapping("/api/coordinator/members")
    public ResponseEntity<?> getAllMembers() {
        List<AddMemberToDepartmentDTO> allMembers = memberService.getAllMembersToAddDepartment();
        return ResponseEntity.ok(allMembers);
    }

    @PostMapping("/api/add/member/department")
    public ResponseEntity<?> addMemberToDepartment(
            @RequestBody AddedMemberToDepartmentDTO addedMembers
    ) {
        String departmentName = addedMembers.departmentName;
        List<Integer> memberIds = addedMembers.memberIds;

        if (departmentName.isEmpty() || memberIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid department ID or member IDs");
        }

        try {
            int departmentId = departmentService.getDepartmentIdByName(departmentName);
            departmentMembersService.addMembersToDepartment(departmentId, memberIds);
        } catch (Exception e) {
           return ResponseEntity.status(500).body("Error adding members to department: " + e.getMessage());
        }
        return ResponseEntity.ok("Members added successfully to " + department);
    }

    @DeleteMapping("/api/delete/member/department/{memberId}")
    public ResponseEntity<?> removeMemberFromDepartment(
            @PathVariable("memberId") int memberId
    ) {
        try {
            departmentMembersService.removeMemberFromDepartment(memberId, department);
            return ResponseEntity.ok("Member removed successfully from " + department);
        } catch (Exception e) {
            System.out.println("===Error removing member from department=== " + e.getMessage());
            return ResponseEntity.status(500).body("Error removing member from department: " + e.getMessage());
        }
    }


}
