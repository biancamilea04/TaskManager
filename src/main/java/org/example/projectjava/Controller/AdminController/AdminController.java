package org.example.projectjava.Controller.AdminController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberRepository;
import org.example.projectjava.Model.Member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    MemberService memberService;

    @PreAuthorize("hasAuthority('COORDONATOR')")
    @GetMapping("/adminASII")
    public String getAdmin() {
        return "admin/admin";
    }

    @PostMapping("/api/members/import")
    @ResponseBody
    public String importMembers(@RequestParam("file") MultipartFile file ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Member> members = mapper.readValue(file.getInputStream(), new TypeReference<List<Member>>() {});

        for(Member member : members) {
            member.setPassword(new BCryptPasswordEncoder().encode(member.getPassword()));
        }
        memberService.saveAll(members);
        return "OK";
    }
}
