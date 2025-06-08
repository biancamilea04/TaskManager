package org.example.projectjava.Model.Member;

import lombok.AllArgsConstructor;
import org.example.projectjava.ControllerDTO.DepartmentDTO.AddMemberToDepartmentDTO;
import org.example.projectjava.ControllerDTO.MembersDTO.MemberDepartmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            return member.get();
        }
        throw new UsernameNotFoundException("User not found");
    }

    public Optional<Member> authenticate(String email, String password) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            if (new BCryptPasswordEncoder().matches(password, member.get().getPassword())) {
                return member;
            }
        }
        return Optional.empty();
    }

    public ResponseEntity<String> isAuthenticated(String email, String password) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        }
        if (!new BCryptPasswordEncoder().matches(password, member.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password");
        }
        return ResponseEntity.status(HttpStatus.OK).body("User authenticated");
    }

    public ResponseEntity<String> isValidMember(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("User valid");
    }

    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public ResponseEntity<String> memberAlreadyExist(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body("User already exist");
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public List<MemberDepartmentDTO> getMembersByDepartment(String departmentName) {
        return memberRepository.findAll().stream()
                .filter(member -> member.getDepartmentMembers().stream().anyMatch(dm -> dm.getDepartment().getName().equals(departmentName)))
                .map(member -> {
                    MemberDepartmentDTO dto = new MemberDepartmentDTO();
                    dto.setId(member.getId());
                    dto.setName(member.getName());
                    dto.setSurname(member.getSurname());
                    dto.setStatus(member.getMemberDetails().getStatus() != null ? member.getMemberDetails().getStatus() : "MEMBER");
                    dto.setActivityHours(member.getMemberDetails().getTotalActivityHours());
                    return dto;
                })
                .toList();
    }

    public List<AddMemberToDepartmentDTO> getAllMembersToAddDepartment() {
        return memberRepository.findAll().stream()
                .map(member -> {
                    AddMemberToDepartmentDTO dto = new AddMemberToDepartmentDTO();
                    dto.setId(member.getId());
                    dto.setName(member.getName());
                    dto.setSurname(member.getSurname());
                    return dto;
                })
                .toList();
    }

    public boolean isCoordinator(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            return member.get().getCoordinatingDepartment()!=null;
        }
        return false;
    }

    public Optional<Member> findById(Integer memberId) {
        return memberRepository.findById(memberId);
    }

    public void saveAll(List<Member> members) {
        if (members != null && !members.isEmpty()) {
            memberRepository.saveAll(members);
        }
    }

    public int getMembersCount() {
        String sql = "{ ? = call get_members_count }";
        return jdbcTemplate.execute(
                (ConnectionCallback<Integer>) (conn) -> {
                    try (var cs = conn.prepareCall(sql)) {
                        cs.registerOutParameter(1, Types.INTEGER);
                        cs.execute();
                        return cs.getInt(1);
                    }
                }
        );
    }

    public int getAllMembersCount() {
        return (int) memberRepository.count();
    }

}
