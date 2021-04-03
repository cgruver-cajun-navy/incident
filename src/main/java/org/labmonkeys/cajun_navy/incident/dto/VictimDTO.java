package org.labmonkeys.cajun_navy.incident.dto;

import lombok.Data;

@Data
public class VictimDTO {
    public enum VictimStatus {PICKEDUP, RESCUED};
    private String victimId;
    private boolean medicalNeeded;
    private String victimName;
    private String victimPhoneNumber;
    private VictimStatus status;
}
