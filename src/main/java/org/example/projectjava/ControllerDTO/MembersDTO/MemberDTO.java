package org.example.projectjava.ControllerDTO.MembersDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MemberDTO {
    Integer id;
    String name;
    String surname;
    String status;
    List<String> departments;
}
