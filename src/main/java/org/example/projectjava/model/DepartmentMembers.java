package org.example.projectjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
