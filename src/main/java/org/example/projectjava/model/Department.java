package org.example.projectjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

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
    @Column(name = "URL")
    private String url;
    @OneToOne
    @JoinColumn(name = "ID_COORDINATOR", referencedColumnName = "ID_MEMBER")
    Member member;
    @OneToMany(mappedBy = "department")
    private List<DepartmentMembers> departmentMembers;

    static public Map<String,String> departmentNames = Map.of(
            "ri", "Relatii Interne",
            "re", "Relatii Externe",
            "it", "IT",
            "prm", "PR&Media",
            "pro","Proiecte",
            "ev","Evaluari"
    );
}
