package org.example.projectjava.Model.Task;

import org.example.projectjava.Model.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Integer> {
    List<Tasks> findAllByMemberOrderByMemberTaskNumberAsc(Member member);

    Tasks findByMemberAndMemberTaskNumber(Member member, int memberTaskNumber);
}
