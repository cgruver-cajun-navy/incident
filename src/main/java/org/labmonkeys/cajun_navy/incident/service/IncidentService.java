package org.labmonkeys.cajun_navy.incident.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.quarkus.runtime.StartupEvent;

import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
import org.labmonkeys.cajun_navy.incident.dto.PriorityZoneDTO;
import org.labmonkeys.cajun_navy.incident.dto.VictimDTO;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO.IncidentStatus;
import org.labmonkeys.cajun_navy.incident.event.IncidentEventPublisher;
import org.labmonkeys.cajun_navy.incident.mapper.IncidentMapper;
import org.labmonkeys.cajun_navy.incident.model.Incident;
import org.labmonkeys.cajun_navy.incident.model.Victim;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@ApplicationScoped
public class IncidentService {

    //private static final Logger log = LoggerFactory.getLogger(IncidentService.class);

    @Inject
    IncidentMapper mapper;
    
    @Inject
    MeterRegistry meterRegistry;

    @Inject
    IncidentEventPublisher publisher;

    private Timer createTimer;

    private Timer updateTimer;

    void onStart(@Observes StartupEvent e) {
        String name = "incident.service.data.access";
        createTimer = Timer.builder(name).tag("operation", "create").register(meterRegistry);
        updateTimer = Timer.builder(name).tag("operation", "update").register(meterRegistry);
    }

    @Transactional
    public List<IncidentDTO> incidents() {
        return mapper.incidentEntitiesToDtos(Incident.findAll().list());
    }

    @Transactional
    public IncidentDTO incidentByIncidentId(String incidentId) {
        return mapper.incidentEntityToDto(Incident.findByIncidentId(incidentId));
    }

    @Transactional
    public IncidentDTO incidentByVictimId(String victimId) {
        Victim entity = Victim.findByVictimId(victimId);
        if (entity ==null) {
            return null;
        }
        return mapper.incidentEntityToDto(entity.getIncident());
    }

    @Transactional
    public List<IncidentDTO> incidentsByStatus(IncidentStatus status) {
        return mapper.incidentEntitiesToDtos(Incident.findByStatus(status));
    }

    @Transactional
    public List<VictimDTO> victimsByVictimName(String name) {
        return mapper.victimEntitiesToDtos(Victim.findByName(name));
    }

    public IncidentDTO create(IncidentDTO incident) {
        try {
            return createTimer.recordCallable(() -> doCreate(incident));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public IncidentDTO doCreate(IncidentDTO dto) {
        Incident entity = mapper.incidentDtoToEntity(dto);
        entity.setIncidentId(UUID.randomUUID().toString());
        Incident.persist(entity);
        IncidentDTO incident = mapper.incidentEntityToDto(entity);
        publisher.reportIncident(incident);
        return incident;
    }

    public IncidentDTO updateIncidentStatus(IncidentDTO incident) {
        try {
            return updateTimer.recordCallable(() -> doUpdateIncidentStatus(incident));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private IncidentDTO doUpdateIncidentStatus(IncidentDTO dto) {
        IncidentDTO incident = mapper.incidentEntityToDto(Incident.updateStatus(dto.getStatus(), dto.getIncidentId()));
        publisher.updateIncident(incident);
        return incident;
    }

    public IncidentDTO updateIncidentPriority(IncidentDTO incident) {
        try {
            return updateTimer.recordCallable(() -> doUpdateIncidentPriority(incident));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private IncidentDTO doUpdateIncidentPriority(IncidentDTO dto) {
        IncidentDTO incident = mapper.incidentEntityToDto(Incident.updatePriority(dto.getPriority(), dto.getIncidentId()));
        publisher.updateIncident(incident);
        return incident;
    }

    public IncidentDTO updateIncidentLocation(IncidentDTO incident) {
        try {
            return updateTimer.recordCallable(() -> doUpdateIncidentLocation(incident));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private IncidentDTO doUpdateIncidentLocation(IncidentDTO dto) {
        IncidentDTO incident = mapper.incidentEntityToDto(Incident.updateLocation(mapper.incidentDtoToEntity(dto)));
        publisher.updateIncident(incident);
        return incident;
    }

    public IncidentDTO addVictim(VictimDTO dto, String incidentId) {
        try {
            return updateTimer.recordCallable(() -> doAddVictim(dto, incidentId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private IncidentDTO doAddVictim(VictimDTO dto, String incidentId) {
        Incident incident = Incident.findByIncidentId(incidentId);
        if (incident == null) {
            return null;
        }
        Victim entity = mapper.victimDtoToEntity(dto);
        entity.setIncident(incident);
        Victim.persist(entity);
        incident.getVictims().add(entity);
        return mapper.incidentEntityToDto(incident);
    }
    
    public IncidentDTO updateVictim(VictimDTO dto) {
        try {
            return updateTimer.recordCallable(() -> doUpdateVictim(dto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    private IncidentDTO doUpdateVictim(VictimDTO dto)
    {
        Victim entity = Victim.updateVictim(mapper.victimDtoToEntity(dto));
        if (entity == null) {
            return null;            
        }
        return mapper.incidentEntityToDto(entity.getIncident());
    }

    @Transactional
    public void reset() {
        Incident.deleteAll();
    }

    public Double evaluateAveragePriority() {
        Double incidentCount = Double.longBitsToDouble(Incident.count());
        Double priorityTotal = 0D;
        List<Incident> incidents = Incident.findAll().list();
        for (Incident incident : incidents) {
            priorityTotal += incident.getPriority().doubleValue();
        }
        return priorityTotal/incidentCount;
    }

    public void increasePriority(String incidentId) {
        Incident incident = Incident.findByIncidentId(incidentId);
        incident.setPriority(incident.getPriority()+1);
    }

    public boolean incidentInZone(IncidentDTO incident, PriorityZoneDTO zone) {
        return zone.getRadius().compareTo(
            new BigDecimal(distance(
                incident.getLatitude().doubleValue(),
                incident.getLongitude().doubleValue(),
                zone.getLatitude().doubleValue(), 
                zone.getLongitude().doubleValue(), "K")
            )
         ) >= 0;
    }

    /**
     * Calculate the distance between two coordinates in latitude and longitude, using the specified units.
     * 
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @param unit the unit of measurement, where K = kilometers, M = miles (defualt), and N = nautical miles
     * @return the distance as a double
     */
    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			if (unit.equals("K")) {
				dist = dist * 1.609344;
			} else if (unit.equals("N")) {
				dist = dist * 0.8684;
			}
			return (dist);
		}
	}
}