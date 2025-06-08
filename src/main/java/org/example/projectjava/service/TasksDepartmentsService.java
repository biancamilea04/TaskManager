package org.example.projectjava.service;

import org.example.projectjava.Model.Department.Department;
import org.example.projectjava.Model.Department.DepartmentRepository;
import org.example.projectjava.Model.Task.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TasksDepartmentsService {
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    TasksRepository tasksRepository;

    public Map<String,Integer> tasksDepartments(){
        Map<String,Integer> tasksDepartments = new HashMap<>();
        List<Department> departments = departmentRepository.findAll();

        departments.forEach(department -> {
            String departmentName = department.getName();
            int idDepartment = department.getId();
            int countTasks = tasksRepository.countTaskByDepartment(department);
            tasksDepartments.put(departmentName,countTasks);
        });

        return tasksDepartments;
    }
}
