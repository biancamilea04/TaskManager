package org.example.projectjava.Model.MemberDetails;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.projectjava.Model.Member.Member;

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
    private String status;
    @Column(name="VOTING_RIGHT")
    private String votingRight;
    @Column(name="TOTAL_ACTIVITY_HOURS")
    private float totalActivityHours;
}
