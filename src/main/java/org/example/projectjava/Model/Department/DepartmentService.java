package org.example.projectjava.Model.Department;

import org.example.projectjava.Model.DepartmentMembers.DepartmentMembers;
import org.example.projectjava.Model.Member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonWriter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public int getDepartmentIdByName(String departmentName) {
        List<Department> departments = departmentRepository.findByName(departmentName);
        if (departments.isEmpty()) {
            return -1;
        }
        return departments.get(0).getId();
    }

    public String getUrlByDepartmentName(String departmentName) {
        List<Department> departments = departmentRepository.findByName(departmentName);
        if (departments.isEmpty()) {
            return null;
        }
        Department department = departments.get(0);
        return department.getUrl();
    }

    public String GetCoordinatorNameByDepartmentName(String departmentName) {
        List<Department> departments = departmentRepository.findByName(departmentName);
        if (departments.isEmpty()) {
            return null;
        }
        Department department = departments.get(0);
        Member coordinator = department.getMember();
        String coordinatorName = coordinator.getName();
        String coordinatorSurname = coordinator.getSurname();

        return coordinatorName + " " + coordinatorSurname;
    }
}
