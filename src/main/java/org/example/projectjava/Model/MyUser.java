package org.example.projectjava.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class MyUser {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;

}
