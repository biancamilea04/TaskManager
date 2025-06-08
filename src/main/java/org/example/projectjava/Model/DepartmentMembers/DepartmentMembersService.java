package org.example.projectjava.Model.DepartmentMembers;

import org.example.projectjava.Model.Department.Department;
import org.example.projectjava.Model.Department.DepartmentService;
import org.example.projectjava.Model.Member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentMembersService {
    @Autowired
    private DepartmentMembersRepository departmentMembersRepository;
    @Autowired
    private DepartmentService departmentService;

    public void addMembersToDepartment(int departmentId, List<Integer> memberIds) {
        for (Integer memberId : memberIds) {
            Department department = new Department();
            department.setId(departmentId);

            Member member = new Member();
            member.setId(memberId);

            DepartmentMembersEmbeddedId id = new DepartmentMembersEmbeddedId();
            id.setDepartmentId(departmentId);
            id.setMemberId(memberId);

            DepartmentMembers departmentMembers = new DepartmentMembers();
            departmentMembers.setId(id);
            departmentMembers.setMember(member);
            departmentMembers.setDepartment(department);

            departmentMembersRepository.save(departmentMembers);
        }
    }

    public void removeMemberFromDepartment(int memberId, String department) {
        DepartmentMembersEmbeddedId id = new DepartmentMembersEmbeddedId();

        int departmentId = departmentService.getDepartmentIdByName(department);
        id.setMemberId(memberId);
        id.setDepartmentId(departmentId);

        DepartmentMembers departmentMembers = new DepartmentMembers();
        departmentMembers.setId(id);

        departmentMembersRepository.delete(departmentMembers);
    }

    public int getMembERsCountByDepartmentName(String departmentName) {
        int departmentId = departmentService.getDepartmentIdByName(departmentName);
        return departmentMembersRepository.countByDepartmentId(departmentId);
    }

    public int getTotalMembers() {
        return (int) departmentMembersRepository.count();
    }
}
