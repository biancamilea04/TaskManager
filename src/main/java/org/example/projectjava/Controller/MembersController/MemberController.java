package org.example.projectjava.Controller.MembersController;

import org.example.projectjava.ControllerDTO.MembersDTO.MemberDTO;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/all")
    public List<MemberDTO> getAllMembers() {
        List<Member> members = memberService.findAll();

        return members.stream().map(
                member -> {
                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setName(member.getName());
                    memberDTO.setSurname(member.getSurname());
                    memberDTO.setStatus(member.getMemberDetails().getStatus() != null
                            ? member.getMemberDetails().getStatus().toString() : "Unknown");
                    if(member.getDepartmentMembers() != null) {
                        List<String> departments = member.getDepartmentMembers().stream()
                                .map(departmentMember -> departmentMember.getDepartment().getName())
                                .collect(Collectors.toList());
                        memberDTO.setDepartments(departments);
                    } else {
                        memberDTO.setDepartments(new ArrayList<>());
                    }
                    return memberDTO;
                }).collect(Collectors.toList());
    }
}
