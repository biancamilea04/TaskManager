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
    private DepartmentMembersEmbeddedId id;

    @ManyToOne
    @MapsId("departmentId")
    @JoinColumn(name = "ID_DEPARTMENT", referencedColumnName = "ID_DEPARTMENT")
    private Department department;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "ID_MEMBER", referencedColumnName = "ID_MEMBER")
    private Member member;
}
