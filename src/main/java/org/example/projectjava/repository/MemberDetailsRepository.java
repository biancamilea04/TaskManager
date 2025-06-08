package org.example.projectjava.repository;

import org.example.projectjava.model.Member;
import org.example.projectjava.model.MemberDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Integer> {
    Optional<MemberDetails> findByMemberId(int memberId);

    MemberDetails findByMember(Member member);
}
