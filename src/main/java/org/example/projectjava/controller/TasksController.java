package org.example.projectjava.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.projectjava.DTO.TaskDTO;
import org.example.projectjava.model.*;
import org.example.projectjava.repository.MemberRepository;
import org.example.projectjava.service.DepartmentMembersService;
import org.example.projectjava.service.DepartmentService;
import org.example.projectjava.service.MemberDetailsService;
import org.example.projectjava.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TasksController {

    @Autowired
    private TasksService tasksService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberDetailsService memberDetailsService;
    @Autowired
    private DepartmentMembersService departmentMembersService;
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/api/tasks")
    public ResponseEntity<?> getAllTasks(HttpServletRequest request) {
        String memberEmail=null;

        if(request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("user")) {
                    memberEmail = cookie.getValue();
                    break;
                }
            }
        }

        if(memberEmail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found in cookie");
        }

        Optional<Member> member = memberRepository.findByEmail(memberEmail);
        if(member.isEmpty()) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Tasks> tasks = tasksService.getAllTasksMember(member.get());
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(task -> {
                    TaskDTO taskDTO = new TaskDTO();
                    taskDTO.title = task.getTitle();
                    taskDTO.description = task.getDescription();
                    taskDTO.dateTask = task.getDateTask();
                    taskDTO.numberActivityHours = task.getNumberActivityHours();
                    taskDTO.status = task.getStatus();
                    taskDTO.memberTaskNumber = task.getMemberTaskNumber();
                    return taskDTO;
                })
                .toList();
        return ResponseEntity.ok(taskDTOs);
    }

    @PostMapping("/api/tasks")
    public ResponseEntity<?> createTask(@RequestBody TaskDTO task, HttpServletRequest request) {
        String memberEmaill = null;
        if(request.getCookies()!=null) {
            for (Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("user")) {
                    memberEmaill = cookie.getValue();
                    break;
                }
            }
        }

        if(memberEmaill==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member member = memberRepository.findByEmail(memberEmaill).orElse(null);
        if(member==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(member.getDepartmentMembers().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nu ai voie sa adaugi taskuri daca nu esti intr-un departament");
        }

        Tasks myTask = new Tasks();
        myTask.setTitle(task.title);
        myTask.setDescription(task.description);
        myTask.setDateTask(task.dateTask);
        myTask.setNumberActivityHours(task.numberActivityHours);
        myTask.setStatus(task.status);
        myTask.setMember(member);
        Department department = departmentService.getDepartmentById(task.departmentId);

        myTask.setDepartment(department);
        tasksService.save(myTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/api/tasks/{memberTaskNumber}")
    public ResponseEntity<?> updateTask(@PathVariable int memberTaskNumber, @RequestBody TaskDTO task, HttpServletRequest request ){
        String memberEmaill = null;
        if(request.getCookies()!=null) {
            for (Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("user")) {
                    memberEmaill = cookie.getValue();
                    break;
                }
            }
        }

        if(memberEmaill==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member member = memberRepository.findByEmail(memberEmaill).orElse(null);
        if(member==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(member.getDepartmentMembers().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nu ai voie sa adaugi taskuri daca nu esti intr-un departament");
        }

        Tasks mytask = tasksService.findByMemberAndMemberTaskNumber(member,memberTaskNumber);

        if(mytask==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        mytask.setTitle(task.title);
        mytask.setDescription(task.description);
        mytask.setDateTask(task.dateTask);
        mytask.setNumberActivityHours(task.numberActivityHours);
        mytask.setStatus(task.status);

        tasksService.save(mytask);

        return ResponseEntity.ok(mytask);
    }

    @DeleteMapping("/api/tasks/{memberTaskNumber}")
    public ResponseEntity<Tasks> deleteTask(@PathVariable int memberTaskNumber, HttpServletRequest request) {
        String memberEmaill = null;
        if(request.getCookies()!=null) {
            for (Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("user")) {
                    memberEmaill = cookie.getValue();
                    break;
                }
            }
        }

        if(memberEmaill==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


        Member member = memberRepository.findByEmail(memberEmaill).orElse(null);
        if(member==null)  return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        MemberDetails memberDetails = member.getMemberDetails();

        Tasks mytask = tasksService.findByMemberAndMemberTaskNumber(member,memberTaskNumber);

        if(mytask==null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if(mytask.getStatus().equals("Finalizat")) {
            memberDetails.setTotalActivityHours(memberDetails.getTotalActivityHours() - mytask.getNumberActivityHours());
            if(memberDetails.getTotalActivityHours() < 30 ){
                memberDetails.setVotingRight("NU");
            }
            memberDetailsService.save(memberDetails);
        }

        tasksService.delete(mytask);

        return ResponseEntity.ok(mytask);
    }

}
