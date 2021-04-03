package org.labmonkeys.cajun_navy.incident.event;

import java.time.OffsetDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.vertx.mutiny.core.eventbus.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
import org.labmonkeys.cajun_navy.incident.dto.VictimDTO;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO.IncidentStatus;
import org.labmonkeys.cajun_navy.incident.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventBusSubscriber {

    private static final Logger log = LoggerFactory.getLogger(EventBusSubscriber.class);

    @Inject
    IncidentService service;

    private final UnicastProcessor<IncidentDTO> processor = UnicastProcessor.create();

    @ConsumeEvent("incidents")
    @Blocking
    public List<IncidentDTO> incidents(Message<Object> msg) {
        return service.incidents();
    }

    @ConsumeEvent("incidentById")
    @Blocking
    public IncidentDTO incidentById(String incidentId) {
        return service.incidentByIncidentId(incidentId);
    }

    @ConsumeEvent("incidentStatus")
    @Blocking
    public List<IncidentDTO> incidentsByStatus(IncidentStatus status) {
        return service.incidentsByStatus(status);
    }

    @ConsumeEvent("victimName")
    @Blocking
    public List<VictimDTO> victimsByName(String name) {
        return service.victimsByVictimName(name);
    }

    @ConsumeEvent("incidentByVictimId")
    @Blocking
    public IncidentDTO incidentByVictimId(String victimId) {
        return service.incidentByVictimId(victimId);
    }

    @ConsumeEvent("createIncident")
    @Blocking
    public IncidentDTO createIncident(IncidentDTO incident) {
        IncidentDTO newIncident = service.create(incident);
        processor.onNext(newIncident);
        return newIncident;
    }

    
    @ConsumeEvent("reset")
    @Blocking
    public void reset(Message<Object> msg) {
        service.reset();
    }

    @Outgoing("incident-reported-event")
    public Multi<org.eclipse.microprofile.reactive.messaging.Message<IncidentDTO>> source() {
        return processor.onItem().transform(this::toMessage);
    }

    private org.eclipse.microprofile.reactive.messaging.Message<IncidentDTO> toMessage(IncidentDTO incident) {
        log.debug("IncidentReportedEvent: " + incident);
        return KafkaRecord.of(incident.getIncidentId(), incident)
                .addMetadata(OutgoingCloudEventMetadata.builder().withType("IncidentReportedEvent")
                        .withTimestamp(OffsetDateTime.now().toZonedDateTime()).build());
    }
}