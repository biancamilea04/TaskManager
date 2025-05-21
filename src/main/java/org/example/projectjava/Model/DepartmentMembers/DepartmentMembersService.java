package org.example.projectjava.Model.DepartmentMembers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentMembersService {
    @Autowired
    private DepartmentMembersRepository departmentMembersRepository;
}
