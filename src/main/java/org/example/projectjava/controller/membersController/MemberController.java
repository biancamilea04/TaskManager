package org.example.projectjava.controller.membersController;

import org.example.projectjava.DTO.MembersDTO.MemberDTO;
import org.example.projectjava.DTO.MembersDTO.MemberDepartmentNameIdDTO;
import org.example.projectjava.DTO.UpdateStatusDTO;
import org.example.projectjava.jwt.JwtService;
import org.example.projectjava.model.DepartmentMembers;
import org.example.projectjava.model.Member;
import org.example.projectjava.service.MemberService;
import org.example.projectjava.model.MemberDetails;
import org.example.projectjava.repository.MemberDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberDetailsRepository memberDetailsRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/all")
    public List<MemberDTO> getAllMembers() {
        List<Member> members = memberService.findAll();

        return members.stream().map(
                member -> {
                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setId(member.getId());
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

    @GetMapping("/voting-right")
    public String getVotingRight(Authentication authentication) {
        String username = authentication.getName();
        Optional<Member> member = memberService.findByEmail(username);
        if(member.isEmpty()) {
            return "Necunoscut";
        }
        MemberDetails memberDetails = memberDetailsRepository.findByMember(member.get());
        return memberDetails != null && memberDetails.getVotingRight() != null
                ? memberDetails.getVotingRight().toString()
                : "Necunoscut";
    }

    @GetMapping("/count")
    public int getMembersCount() {
        return memberService.getMembersCount();
    }

    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String jwt = authHeader.substring(7);
        String email;
        try {
            email = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT invalid!");
        }

        Member member = memberService.findByEmail(email).orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Member not found!");
        }

        List<DepartmentMembers> departments = member.getDepartmentMembers();
        if(departments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found!");
        }

        List<MemberDepartmentNameIdDTO> departmentsList = departments.stream()
                .map(department -> {
                    MemberDepartmentNameIdDTO dto = new MemberDepartmentNameIdDTO();
                    dto.id = department.getId().getDepartmentId();
                    dto.name = department.getDepartment().getName();
                    return dto;
                  }
                ).toList();

        return ResponseEntity.ok(departmentsList);
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateMemberStatus(@RequestBody UpdateStatusDTO updateStatusDTO) {
        List<Integer> memberIds = updateStatusDTO.memberIds;
        String status = updateStatusDTO.status;

        if (memberIds == null || memberIds.isEmpty() || status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body("Unknown parameters");
        }

        int updatedCount = 0;
        for (Integer memberId : memberIds) {
            Optional<Member> memberOpt = memberService.findById(memberId);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                MemberDetails details = memberDetailsRepository.findByMember(member);
                if (details != null) {
                     details.setStatus(status);
                    memberDetailsRepository.save(details);
                    updatedCount++;
                }
            }
        }
        if (updatedCount == 0) {
            return ResponseEntity.status(404).body("Niciun membru nu a fost actualizat!");
        }
        return ResponseEntity.ok("Status actualizat cu succes pentru " + updatedCount + " membri.");
    }

}
