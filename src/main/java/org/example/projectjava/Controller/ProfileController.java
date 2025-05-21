package org.example.projectjava.Controller;

import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberService;
import org.example.projectjava.Model.MemberDetails.MemberDetails;
import org.example.projectjava.Model.MemberDetails.MemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

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
            System.out.println("[get humanDetails] " +member.getName() + " " + member.getEmail());
            return ResponseEntity.ok(member);
    }

    @GetMapping("/api/profile/memberDetails")
    public ResponseEntity<MemberDetails> getProfileDetails(
            @CookieValue(value = "user", defaultValue = "")
            String email
    ){
        if(email.isEmpty()) {
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
}
