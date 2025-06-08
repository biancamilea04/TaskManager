package org.example.projectjava.DTO.DepartmentDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentStatsDTO {
    private int nrTaskuri;
    private int nrTaskuriFinalizate;
    private float performanta;
}
