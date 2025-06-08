package org.example.projectjava.DTO.DepartmentDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddedMemberToDepartmentDTO {
    public List<Integer> memberIds;
    public String departmentName;

}
