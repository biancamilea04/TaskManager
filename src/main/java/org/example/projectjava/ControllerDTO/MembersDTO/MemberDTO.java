package org.example.projectjava.ControllerDTO.MembersDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MemberDTO {
    String name;
    String surname;
    String status;
    List<String> departments;
}
