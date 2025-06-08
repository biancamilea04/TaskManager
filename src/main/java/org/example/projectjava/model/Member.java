package org.example.projectjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "MEMBERS")
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "membri_seq_gen")
    @SequenceGenerator(name = "membri_seq_gen", sequenceName = "membri_seq", allocationSize = 1)
    @Column(name = "ID_MEMBER")
    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private MemberDetails memberDetails;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepartmentMembers> departmentMembers;

    @OneToOne(mappedBy="member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Department coordinatingDepartment;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tasks> tasks;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String status = memberDetails != null ? memberDetails.getStatus() : "MEMBER";
        return List.of(new SimpleGrantedAuthority(status));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
