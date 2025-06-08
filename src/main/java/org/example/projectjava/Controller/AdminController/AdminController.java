package org.example.projectjava.Controller.AdminController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectjava.ControllerDTO.MembersDTO.MembersExportDTO;
import org.example.projectjava.ControllerDTO.MembersDTO.MembersImportDTO;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COORDINATOR')")
    @GetMapping("/adminASII")
    public String getAdmin() {
        return "admin/admin";
    }

    @PostMapping("/api/members/import")
    @ResponseBody
    public ResponseEntity<String> importMembers(@RequestParam("file") MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<MembersImportDTO> members = mapper.readValue(file.getInputStream(), new TypeReference<List<MembersImportDTO>>() {
            });
            List<Member> importMembers = members.stream()
                    .map(memberDTO -> {
                        Member member = new Member();
                        member.setName(memberDTO.name);
                        member.setSurname(memberDTO.surname);
                        String email = memberDTO.name.toLowerCase() + "." + memberDTO.surname.toLowerCase() + "@asii.ro";
                        member.setEmail(email);
                        member.setPassword(new BCryptPasswordEncoder().encode(memberDTO.name.substring(0, 1).toLowerCase()));
                        return member;
                    })
                    .toList();
            memberService.saveAll(importMembers);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Eroare la procesarea fisierului: " + e.getMessage());
        }

        return ResponseEntity.ok("Membrii au fost incarcati cu succes!");
    }

    @GetMapping("/api/members/export")
    @ResponseBody
    public ResponseEntity<Resource> exportMembers() {
        List<Member> members = memberService.findAll();
        List<MembersExportDTO> membersDTO = members.stream()
                .map(member -> {
                    MembersExportDTO dto = new MembersExportDTO();
                    dto.name = member.getName();
                    dto.surname = member.getSurname();
                    dto.email = member.getEmail();
                    dto.password = member.getPassword();
                    return dto;
                })
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(membersDTO);
            ByteArrayResource resource = new ByteArrayResource(json.getBytes());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Content-Disposition", "attachment; filename=\"members.json\"")
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
