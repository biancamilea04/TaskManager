package org.example.projectjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "TASKS")
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq_gen")
    @SequenceGenerator(name = "task_seq_gen", sequenceName = "task_seq", allocationSize = 1)
    @Column(name = "ID_TASK")
    private int id;
    private String title;
    private String description;
    private LocalDate dateTask;
    private Float numberActivityHours;
    private String status;
    @Column(name = "MEMBER_TASK_NUMBER")
    private int memberTaskNumber;
    @ManyToOne
    @JoinColumn(name = "ID_MEMBER", nullable = false)
    private Member member;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DEPARTMENT", nullable = false)
    private Department department;
}
