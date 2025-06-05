package org.example.projectjava.Controller;

import org.example.projectjava.ControllerDTO.ChangePasswordDTO;
import org.example.projectjava.ControllerDTO.DepartmentDTO.DepartmentDTO;
import org.example.projectjava.ControllerDTO.MemberProfileDTO;
import org.example.projectjava.ControllerDTO.MembersDTO.MemberDepartmentDTO;
import org.example.projectjava.ControllerDTO.MembersDTO.MemberDetailsDTO;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberService;
import org.example.projectjava.Model.MemberDetails.MemberDetails;
import org.example.projectjava.Model.MemberDetails.MemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ProfileController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberDetailsService memberDetailsService;


    @GetMapping("/profile")
    public String profilePage() {
        return "Profile/profile";
    }

    @GetMapping("/api/profile/humanDetails")
    public ResponseEntity<MemberDetailsDTO> getProfileHumanDetails(
            @CookieValue(value = "user", defaultValue = "")
            String email
    ) {
        if (email.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        Optional<Member> memberOptional = memberService.findByEmail(email);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Member member = memberOptional.get();
        Optional<MemberDetails> memberDetailsOptional = memberDetailsService.findByMemberId(member.getId());
        if (memberDetailsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MemberDetails memberDetails = memberDetailsOptional.get();
        MemberDetailsDTO  humanDTO = new MemberDetailsDTO ();
        humanDTO.address = memberDetails.getAddress();
        humanDTO.phone = memberDetails.getPhone();
        humanDTO.cnp = memberDetails.getCnp();
        humanDTO.numar = memberDetails.getNumar();
        humanDTO.serie = memberDetails.getSerie();
        System.out.println("[get humanDetails] " + member.getName() + " " + member.getEmail());

        return ResponseEntity.ok(humanDTO);
    }

    @GetMapping("/api/profile/userData")
    public ResponseEntity<MemberProfileDTO> getUserProfile(
            @CookieValue(value = "user", defaultValue = "") String email
    ) {
        if (email.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<Member> memberOptional = memberService.findByEmail(email);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member member = memberOptional.get();
        Optional<MemberDetails> memberDetailsOptional = memberDetailsService.findByMemberId(member.getId());
        if (memberDetailsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MemberDetails memberDetails = memberDetailsOptional.get();

        MemberProfileDTO memberProfile = new MemberProfileDTO();
        memberProfile.address = memberDetails.getAddress();
        memberProfile.phone = memberDetails.getPhone();
        memberProfile.cnp = memberDetails.getCnp();
        memberProfile.numar = memberDetails.getNumar();
        memberProfile.serie = memberDetails.getSerie();
        memberProfile.name = member.getName();
        memberProfile.surname = member.getSurname();
        memberProfile.email = member.getEmail();

        memberProfile.status = member.getMemberDetails().getStatus() != null
                ? member.getMemberDetails().getStatus().toString() : "Unknown";

        memberProfile.votingRight = member.getMemberDetails().getVotingRight() != null
                && member.getMemberDetails().getVotingRight().equals("DA");
        memberProfile.totalHours = member.getMemberDetails().getTotalActivityHours();

        return ResponseEntity.ok(memberProfile);
    }

    @PutMapping("/api/profile/changePassword")
    public ResponseEntity<String> changePassword(
            @CookieValue(value = "user", defaultValue = "")
            String email,
            @RequestBody ChangePasswordDTO changePassword
    ) {
        if (email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Empty email");
        }

        if (changePassword.currentPassword.isEmpty() || changePassword.newPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Empty password");
        }

        Optional<Member> memberOptional = memberService.findByEmail(email);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Member not found");
        }

        Member member = memberOptional.get();
        if (!new BCryptPasswordEncoder().matches(changePassword.currentPassword, member.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password");
        }
        member.setPassword(new BCryptPasswordEncoder().encode(changePassword.newPassword));
        memberService.saveMember(member);
        return ResponseEntity.ok("Password changed successfully");
    }

    @DeleteMapping("/api/profile/deleteAccount")
    public ResponseEntity<String> deleteAccount(
            @CookieValue(value = "user", defaultValue = "")
            String email
    ) {
        if (email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Empty email");
        }

        Optional<Member> memberOptional = memberService.findByEmail(email);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Member not found");
        }

        Member member = memberOptional.get();
        memberService.deleteMember(member);
        return ResponseEntity.ok("Account deleted successfully");
    }

}
