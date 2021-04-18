package org.labmonkeys.cajun_navy.incident.model;

import java.time.Instant;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO.IncidentStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "reported_incident")
public class Incident extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @Column(name = "incident_id", updatable = false, nullable = false, unique = true)
    private String incidentId;

    @Column(name = "disaster_id", updatable = false, nullable = false)
    private String disasterId;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Basic
    @Column(name = "reported_time", updatable = false, nullable = false)
    private Instant reportedTime;

    @Column(name = "incident_status")
    private IncidentStatus status;

    @Column(name = "incident_priority")
    private Integer priority;

    @Column(name = "escalated")
    private boolean escalated;

    @Column(name = "version")
    @Version
    private long version;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "incidentId", cascade = CascadeType.ALL)
    private List<Victim> victims;

    public static List<Incident> findByStatus(IncidentStatus status) {
    
        return find("status", status).list();
    }

    public static Incident findByIncidentId(String incidentId) {
        return find("incidentId", incidentId).firstResult();
    }

    public static Incident updateLocation(Incident entity) {
        update("latitude = ?1, longitude = ?2 where incidentId = ?3", entity.getLatitude(), entity.getLongitude(), entity.getIncidentId());
        return find("incidentId", entity.getIncidentId()).firstResult();
    }

    public static Incident updateStatus(IncidentStatus status, String incidentId) {
        update("status = ?1 where incidentId = ?2", status, incidentId);
        return find("incidentId", incidentId).firstResult();
    }

    public static Incident updatePriority(Integer priority, String incidentId) {
        update("priority = ?1 where incidentId = ?2", priority, incidentId);
        return find("incidentId", incidentId).firstResult();
    }
}
