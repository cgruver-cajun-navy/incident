package org.labmonkeys.cajun_navy.incident.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.labmonkeys.cajun_navy.incident.dto.VictimDTO.VictimStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "incident_victim")
public class Victim extends PanacheEntityBase {

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column(name = "victim_id", updatable = false, nullable = false, unique = true)
    private String victimId;

    @Column(name = "medical_needed")
    private boolean medicalNeeded;

    @Column(name = "victim_name")
    private String victimName;

    @Column(name = "victim_phone")
    private String victimPhoneNumber;

    @Column(name = "victim_status")
    private VictimStatus status;

    @Column(name = "shelter_name")
    private String shelterName;

    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    public static Victim findByVictimId(String victimId) {
        return find("victimId", victimId).firstResult();
    }

    public static List<Victim> findByName(String name) {
        return find("SELECT i from Victim i WHERE LOWER(i.victimName) LIKE :name", name.toLowerCase()).list();
    }

    public static Victim updateVictim(Victim victim) {
        update("medical_needed = ?1, victim_name = ?2, victim_phone = ?3, victim_status = ?4 where incidentId = ?2", victim.isMedicalNeeded(), victim.getVictimName(), victim.getVictimPhoneNumber(), victim.getStatus(), victim.getVictimId());
        return find("victimId", victim.getVictimId()).firstResult();
    }
}
