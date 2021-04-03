package org.labmonkeys.cajun_navy.incident.dto;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class IncidentDTO {

    public enum IncidentStatus {REPORTED, ASSIGNED, CANCELLED, CLOSED};

    private String incidentId;
    private Double latitude;
    private Double longitude;
    private int numberOfPeople;
    private Instant reportedTime;
    private IncidentStatus status;
    private List<VictimDTO> victims;

}
