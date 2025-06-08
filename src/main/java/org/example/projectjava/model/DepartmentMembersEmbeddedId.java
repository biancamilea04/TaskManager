package org.example.projectjava.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Setter
@Getter
public class DepartmentMembersEmbeddedId implements Serializable {

    @Column(name = "ID_DEPARTMENT")
    private int departmentId;
    @Column(name = "ID_MEMBER")
    private int memberId;
}
