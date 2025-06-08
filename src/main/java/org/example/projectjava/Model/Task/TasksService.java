package org.example.projectjava.Model.Task;

import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.projectjava.Model.Member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TasksService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TasksRepository tasksRepository;

    @Transactional
    public void delete(Tasks task) {
        tasksRepository.delete(task);
    }

    public void save(Tasks task) {
        tasksRepository.save(task);
    }

    @Transactional
    public void refresh(Tasks task) {
        entityManager.refresh(task);
    }

    public List<Tasks> getAllTasksMember(Member member) {
        return tasksRepository.findAllByMemberOrderByMemberTaskNumberAsc(member);
    }

    public Tasks findByMemberAndMemberTaskNumber(Member member, int memberTaskNumber) {
        return tasksRepository.findByMemberAndMemberTaskNumber(member, memberTaskNumber);
    }

    public Map<LocalDate,Long> tasksPerDay(){
        List<Tasks> tasks = tasksRepository.findAll();
        return tasks.stream()
                .collect(
                        Collectors.groupingBy(
                                Tasks::getDateTask,
                                Collectors.counting()
                        )
                );
    }
}
