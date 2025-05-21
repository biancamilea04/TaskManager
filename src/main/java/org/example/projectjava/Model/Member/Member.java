package org.example.projectjava.Model.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MEMBERS")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "membri_seq_gen")
    @SequenceGenerator(name = "membri_seq_gen", sequenceName = "membri_seq", allocationSize = 1)
    @Column(name = "ID_MEMBER")
    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;
}
