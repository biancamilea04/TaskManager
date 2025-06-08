package org.example.projectjava.service;

import org.example.projectjava.model.MemberDetails;
import org.example.projectjava.repository.MemberDetailsRepository;
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

    public void save(MemberDetails memberDetails) {
        if (memberDetails == null) {
            throw new IllegalArgumentException("MemberDetails cannot be null");
        }
        memberDetailsRepository.save(memberDetails);
    }

    public long countMembersVotingRight(String votingRight) {
        return memberDetailsRepository.findAll().stream()
                .filter(
                        memberDetails
                                -> votingRight.equals(memberDetails.getVotingRight()))
                .count();
    }
}
