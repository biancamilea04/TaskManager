package org.example.projectjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MEMBER_DETAILS")
public class MemberDetails {
    @Id
    @Column(name= "MEMBER_ID")
    private int memberId;
    @OneToOne
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "ID_MEMBER")
    private Member member;
    private String phone;
    private String address;
    @Column(name = "STATUS")
    private String status;
    @Column(name="VOTING_RIGHT")
    private String votingRight;
    @Column(name="TOTAL_ACTIVITY_HOURS")
    private float totalActivityHours;
    private String cnp;
    private String numar;
    private String serie;
}
