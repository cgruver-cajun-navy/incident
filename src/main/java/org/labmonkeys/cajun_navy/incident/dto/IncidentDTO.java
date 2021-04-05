package org.labmonkeys.cajun_navy.incident.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class IncidentDTO {

    public enum IncidentStatus {REPORTED, ASSIGNED, CANCELLED, CLOSED};

    private String incidentId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private int numberOfPeople;
    private Instant reportedTime;
    private IncidentStatus status;
    private List<VictimDTO> victims;

}
