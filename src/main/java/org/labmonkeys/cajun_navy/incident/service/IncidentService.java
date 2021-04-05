package org.labmonkeys.cajun_navy.incident.service;

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
import org.labmonkeys.cajun_navy.incident.dto.VictimDTO;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO.IncidentStatus;
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
        return mapper.incidentEntityToDto(entity);
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
        
        return mapper.incidentEntityToDto(Incident.updateStatus(dto.getStatus(), dto.getIncidentId()));
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

        Incident entity = mapper.incidentDtoToEntity(dto);
        return mapper.incidentEntityToDto(Incident.updateLocation(entity));
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

}