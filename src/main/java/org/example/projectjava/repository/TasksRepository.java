package org.example.projectjava.repository;

import org.example.projectjava.model.Department;
import org.example.projectjava.model.Member;
import org.example.projectjava.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Integer> {
    List<Tasks> findAllByMemberOrderByMemberTaskNumberAsc(Member member);

    Tasks findByMemberAndMemberTaskNumber(Member member, int memberTaskNumber);

    int countTaskByDepartment(Department department);

    Map<LocalDate, Integer> countTasksByDateTask(LocalDate dateTask);

    Map<LocalDate, Integer> countTasksByDateTaskBefore(LocalDate now);
}
