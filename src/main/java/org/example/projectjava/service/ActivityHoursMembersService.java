package org.example.projectjava.service;

import org.example.projectjava.Model.Member.Member;
import org.example.projectjava.Model.Member.MemberRepository;
import org.example.projectjava.Model.Member.MemberService;
import org.example.projectjava.Model.MemberDetails.MemberDetailsRepository;
import org.example.projectjava.Model.Task.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
