package org.example.projectjava.Model.MemberDetails;

import org.example.projectjava.Model.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Integer> {
    Optional<MemberDetails> findByMemberId(int memberId);

    MemberDetails findByMember(Member member);
}
