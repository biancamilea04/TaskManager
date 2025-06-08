package org.example.projectjava.controller;

import org.example.projectjava.DTO.RegisterDTO;
import org.example.projectjava.model.Member;
import org.example.projectjava.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class RegisterController {

    @Autowired
    private MemberService myMemberService;

    @GetMapping("/register")
    public String registerPage() {
        return "registerPage";
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody RegisterDTO Member) {

       ResponseEntity<String> isValidMember = myMemberService.memberAlreadyExist(Member.email);
        if(isValidMember.getStatusCode().equals(HttpStatus.CONFLICT)) {
            return isValidMember;
        }

        Member myMember = new Member();
        myMember.setName(Member.name);
        myMember.setSurname(Member.surname);
        myMember.setEmail(Member.email);
        myMember.setPassword(new BCryptPasswordEncoder().encode(Member.password));

        try {
            myMemberService.saveMember(myMember);
            return ResponseEntity.ok("Inregistrare reusita");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
