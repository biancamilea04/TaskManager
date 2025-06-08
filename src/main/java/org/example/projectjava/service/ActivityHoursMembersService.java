package org.example.projectjava.service;

import org.example.projectjava.model.Member;
import org.example.projectjava.repository.MemberDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityHoursMembersService {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberDetailsRepository memberDetailsRepository;

    public Map<String, Float> activityHoursMembers(){
        List<Member> members = memberService.findAll();
        Map<String, Float> activityHours = new HashMap<>();

        members.forEach(member -> {
            String memberFullName = member.getName() + " " + member.getSurname();
            float  activityHoursNumber = memberDetailsRepository.findByMember(member).getTotalActivityHours();
            activityHours.put(memberFullName, activityHoursNumber);
        });

        return activityHours;
    }
}
