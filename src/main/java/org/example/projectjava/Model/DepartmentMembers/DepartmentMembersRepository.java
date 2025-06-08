package org.example.projectjava.Model.DepartmentMembers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentMembersRepository extends JpaRepository<DepartmentMembers, Integer> {
    int countByDepartmentId(int departmentId);
}
