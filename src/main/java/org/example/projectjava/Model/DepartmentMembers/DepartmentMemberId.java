package org.example.projectjava.Model.DepartmentMembers;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DepartmentMemberId {
    @Column(name = "ID_MEMBER")
    private int memberId;

    @Column(name = "ID_DEPARTMENT")
    private int departmentId;
}
