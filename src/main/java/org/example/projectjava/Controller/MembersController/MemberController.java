package org.example.projectjava.Controller.MembersController;

import org.example.projectjava.ControllerDTO.MembersDTO.MemberDTO;
import org.example.projectjava.ControllerDTO.UpdateStatusDTO;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberService;
import org.example.projectjava.Model.MemberDetails.MemberDetails;
import org.example.projectjava.Model.MemberDetails.MemberDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/update-status")
    public ResponseEntity<?> updateMemberStatus(@RequestBody UpdateStatusDTO updateStatusDTO) {
        List<Integer> memberIds = updateStatusDTO.memberIds;
        String status = updateStatusDTO.status;

        if (memberIds == null || memberIds.isEmpty() || status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body("Lipsesc parametri!");
        }

        int updatedCount = 0;
        for (Integer memberId : memberIds) {
            System.out.println("Updating status for member ID: " + memberId);
            Optional<Member> memberOpt = memberService.findById(memberId);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                MemberDetails details = memberDetailsRepository.findByMember(member);
                if (details != null) {
                    System.out.println("Updating status for member: " + member.getName() + " " + member.getSurname());
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

    @GetMapping("/count")
    public int getMembersCount() {
        return memberService.getMembersCount();
    }
}
