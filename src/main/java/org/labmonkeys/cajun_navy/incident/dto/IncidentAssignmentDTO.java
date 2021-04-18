package org.labmonkeys.cajun_navy.incident.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class IncidentAssignmentDTO {

    private String incident;
    private Boolean assigned;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
