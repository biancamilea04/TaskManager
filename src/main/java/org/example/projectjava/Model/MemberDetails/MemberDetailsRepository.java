package org.example.projectjava.Model.MemberDetails;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Integer> {
    Optional<MemberDetails> findByMemberId(int memberId);
}
