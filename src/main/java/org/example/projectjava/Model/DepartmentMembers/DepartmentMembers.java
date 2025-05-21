package org.example.projectjava.Model.DepartmentMembers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.projectjava.Model.Department.Department;
import org.example.projectjava.Model.Member.Member;

import javax.persistence.JoinColumn;

@Getter
@Setter
@Entity
@Table(name= "DEPARTMENT_MEMBERS")
public class DepartmentMembers {

    @EmbeddedId
    private DepartmentMemberId id;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "ID_MEMBER")
    private Member member;

    @ManyToOne
    @MapsId("departmentId")
    @JoinColumn(name = "ID_DEPARTMENT")
    private Department department;
}
