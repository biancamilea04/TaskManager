package org.example.projectjava.controller.departmentController;

import org.example.projectjava.DTO.DepartmentDTO.DepartmentDTO;
import org.example.projectjava.DTO.DepartmentDTO.DepartmentStatsDTO;
import org.example.projectjava.model.Department;
import org.example.projectjava.service.DepartmentService;
import org.example.projectjava.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DepartmentRestController {
    @Autowired
    DepartmentService departmentService;
    @Autowired
    private MemberService memberService;

    @GetMapping("/api/departments/id/{departmentName}")
    public int getDepartmentId(
            @PathVariable String departmentName
    ) {
        String department = Department.departmentNames.get(departmentName);
        return departmentService.getDepartmentIdByName(department);
    }

    @GetMapping("/api/departments/name/{departmentName}")
    public String getDepartmentName( @PathVariable String departmentName ) {
        return Department.departmentNames.get(departmentName);
    }

    @GetMapping("/api/departments/info")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentInfo( ) {
        List<DepartmentDTO> departmentInfo = new ArrayList<>();

        Department.departmentNames.forEach((departName, departmentName) -> {
            DepartmentDTO departmentDTO = new DepartmentDTO();
            int id = departmentService.getDepartmentIdByName(departmentName);
            departmentDTO.name = departmentName;
            departmentDTO.shortName = departName;
            departmentDTO.url = departmentService.getUrlByDepartmentName(departmentName);
            departmentDTO.coordinatorName = departmentService.GetCoordinatorNameByDepartmentName(departmentName);
            departmentDTO.memberCount = memberService.getMembersByDepartment(departmentName).size();
            departmentDTO.percentTaskDone= departmentService.getPercentTaskDoneByDepartmentId(id);
            departmentInfo.add(departmentDTO);
        });

        return ResponseEntity.ok(departmentInfo);
    }

    @GetMapping("/api/departments/stats/{departmentName}")
    public ResponseEntity<DepartmentStatsDTO>  getDepartmentStats(@PathVariable String departmentName){
        DepartmentStatsDTO departmentStatsDTO;
        departmentStatsDTO= departmentService.getDepartmentStatsDTOByDepartmentId(departmentService.getDepartmentIdByName(Department.departmentNames.get(departmentName)));
        if (departmentStatsDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(departmentStatsDTO);
    }

}
