package org.example.projectjava.Model.Member;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

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
}
