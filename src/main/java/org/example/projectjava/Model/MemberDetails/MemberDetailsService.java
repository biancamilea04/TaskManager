package org.example.projectjava.Model.MemberDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberDetailsService {
    @Autowired
    private MemberDetailsRepository memberDetailsRepository;

    public Optional<MemberDetails> findByMemberId(int memberId) {
        return memberDetailsRepository.findByMemberId(memberId);
    }
}
