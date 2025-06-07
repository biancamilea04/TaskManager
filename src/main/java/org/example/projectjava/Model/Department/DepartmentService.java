package org.example.projectjava.Model.Department;

import org.example.projectjava.ControllerDTO.DepartmentDTO.DepartmentStatsDTO;
import org.example.projectjava.Model.DepartmentMembers.DepartmentMembers;
import org.example.projectjava.Model.Member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonWriter;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    public float getPercentTaskDoneByDepartmentId(int departmentId) {
        String sql = "{ ? = call get_dpt_completion_percent(?) }";
        return jdbcTemplate.execute(
                (ConnectionCallback<Float>) conn -> {
                    try (var cs = conn.prepareCall(sql)) {
                        cs.registerOutParameter(1, Types.FLOAT); // or Types.NUMERIC
                        cs.setInt(2, departmentId);
                        cs.execute();
                        return cs.getFloat(1);
                    }
                }
        );
    }

    public DepartmentStatsDTO getDepartmentStatsDTOByDepartmentId(int departmentId) {
        String sql1 = "{ ? = call get_done_tasks_by_department(?) }";
        String sql2 = "{ ? = call get_task_count_by_department(?) }";
        String sql3 = "{ ? = call get_department_performance(?) }";
        DepartmentStatsDTO stats = new DepartmentStatsDTO();
        stats.setNrTaskuri(
                jdbcTemplate.execute(
                        (ConnectionCallback<Integer>) conn -> {
                            try (var cs = conn.prepareCall(sql2)) {
                                cs.registerOutParameter(1, Types.INTEGER);
                                cs.setInt(2, departmentId);
                                cs.execute();
                                return cs.getInt(1);
                            }
                        }
                )
        );
        stats.setNrTaskuriFinalizate(
                jdbcTemplate.execute(
                        (ConnectionCallback<Integer>) conn -> {
                            try (var cs = conn.prepareCall(sql1)) {
                                cs.registerOutParameter(1, Types.INTEGER);
                                cs.setInt(2, departmentId);
                                cs.execute();
                                return cs.getInt(1);
                            }
                        }
                )
        );
        stats.setPerformanta(
                jdbcTemplate.execute(
                        (ConnectionCallback<Float>) conn -> {
                            try (var cs = conn.prepareCall(sql3)) {
                                cs.registerOutParameter(1, Types.FLOAT); // or Types.NUMERIC
                                cs.setInt(2, departmentId);
                                cs.execute();
                                return cs.getFloat(1);
                            }
                        }
                )
        );
        System.out.println(stats.getNrTaskuri()+" "+stats.getNrTaskuriFinalizate()+" "+stats.getPerformanta());
        return stats;
    }
}
