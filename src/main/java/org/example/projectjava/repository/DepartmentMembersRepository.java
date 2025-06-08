package org.example.projectjava.repository;

import org.example.projectjava.model.Department;
import org.example.projectjava.model.DepartmentMembers;
import org.example.projectjava.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentMembersRepository extends JpaRepository<DepartmentMembers, Integer> {
    int countByDepartmentId(int departmentId);

}
