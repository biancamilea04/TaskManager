package org.example.projectjava.Controller;

import org.example.projectjava.ControllerDTO.ChangePasswordDTO;
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
    public ResponseEntity<Member> getProfileHumanDetails(
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
        System.out.println("[get humanDetails] " + member.getName() + " " + member.getEmail());
        return ResponseEntity.ok(member);
    }

    @GetMapping("/api/profile/memberDetails")
    public ResponseEntity<MemberDetails> getProfileDetails(
            @CookieValue(value = "user", defaultValue = "")
            String email
    ) {
        if (email.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        Optional<Member> memberOptional = memberService.findByEmail(email);
        if (memberOptional.isEmpty()) {
            System.out.println("[get memberDetails] Member not found for email: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Member member = memberOptional.get();
        Optional<MemberDetails> memberDetailsOptional = memberDetailsService.findByMemberId(member.getId());
        if (memberDetailsOptional.isEmpty()) {
            System.out.println("[get memberDetails] MemberDetails not found for memberId: " + member.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MemberDetails memberDetails = memberDetailsOptional.get();
        System.out.println("[get memberDetails] " + memberDetails.getMemberId() + " " + memberDetails.getTotalActivityHours());
        return ResponseEntity.ok(memberDetails);
    }

    @PutMapping("/api/profile/updateDetails")
    public ResponseEntity<MemberDetails> updateProfileDetails(
            @CookieValue(value = "user", defaultValue = "")
            String email,
            @RequestBody MemberDetailsDTO memberDetails
    ) {
        if (email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<Member> memberOptional = memberService.findByEmail(email);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Member member = memberOptional.get();
        Optional<MemberDetails> existingMemberDetailsOptional = memberDetailsService.findByMemberId(member.getId());
        if (existingMemberDetailsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MemberDetails existingMemberDetails = existingMemberDetailsOptional.get();

        existingMemberDetails.setPhone(memberDetails.phone);
        existingMemberDetails.setAddress(memberDetails.address);
        existingMemberDetails.setCnp(memberDetails.cnp);
        existingMemberDetails.setNumar(memberDetails.numar);
        existingMemberDetails.setSerie(memberDetails.serie);

        memberDetailsService.saveMemberDetails(existingMemberDetails);

        return ResponseEntity.ok(existingMemberDetails);
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
