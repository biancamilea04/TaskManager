package org.example.projectjava.Model.Department;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.projectjava.Model.Member.Member;

@Getter
@Setter
@Entity
@Table(name= "DEPARTMENTS")
public class Department {

    @Id
    @Column(name= "ID_DEPARTMENT")
    private int id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ID_COORDINATOR")
    @OneToOne
    @JoinColumn(name = "ID_COORDINATOR", referencedColumnName = "ID_MEMBER")
    Member member;
}
