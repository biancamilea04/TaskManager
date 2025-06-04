package org.example.projectjava.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.projectjava.ControllerDTO.TaskDTO;
import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberRepository;
import org.example.projectjava.Model.Task.Tasks;
import org.example.projectjava.Model.Task.TasksService;
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
        tasks.forEach(task -> System.out.println(task.getNumberActivityHours()));
        return ResponseEntity.ok(taskDTOs);
    }

    @PostMapping("/api/tasks")
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO task, HttpServletRequest request) {
       System.out.println("[post]Task: " + task.title);
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

        System.out.println(memberEmaill);
        Member member = memberRepository.findByEmail(memberEmaill).orElse(null);
        if(member==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("Id: "+member.getId());

        Tasks myTask = new Tasks();
        myTask.setTitle(task.title);
        myTask.setDescription(task.description);
        myTask.setDateTask(task.dateTask);
        myTask.setNumberActivityHours(task.numberActivityHours);
        myTask.setStatus(task.status);
        myTask.setMember(member);

        tasksService.save(myTask);
        tasksService.refresh(myTask);

        System.out.println(myTask.getMemberTaskNumber());

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/api/tasks/{memberTaskNumber}")
    public ResponseEntity<Tasks> updateTask(@PathVariable int memberTaskNumber, @RequestBody TaskDTO task, HttpServletRequest request ){
        System.out.println("[put]Task: " + task.title);
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

        Tasks mytask = tasksService.findByMemberAndMemberTaskNumber(member,memberTaskNumber);

        if(mytask==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        mytask.setTitle(task.title);
        mytask.setDescription(task.description);
        mytask.setDateTask(task.dateTask);
        mytask.setNumberActivityHours(task.numberActivityHours);
        mytask.setStatus(task.status);

        System.out.println(mytask.getId() + " " + mytask.getTitle() + " " + mytask.getDescription() + " " + mytask.getMemberTaskNumber() + " " + mytask.getNumberActivityHours());

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

        if(memberEmaill==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member member = memberRepository.findByEmail(memberEmaill).orElse(null);
        if(member==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Tasks mytask = tasksService.findByMemberAndMemberTaskNumber(member,memberTaskNumber);

        if(mytask==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        tasksService.delete(mytask);

        return ResponseEntity.ok(mytask);
    }
}
