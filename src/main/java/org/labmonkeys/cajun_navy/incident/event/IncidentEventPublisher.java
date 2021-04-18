package org.labmonkeys.cajun_navy.incident.event;

import javax.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
public class IncidentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventBusSubscriber.class);

    private final UnicastProcessor<IncidentDTO> incidentCreateProcessor = UnicastProcessor.create();
    private final UnicastProcessor<IncidentDTO> incidentUpdateProcessor = UnicastProcessor.create();

    public void reportIncident(IncidentDTO dto) {
        incidentCreateProcessor.onNext(dto);
    }

    public void updateIncident(IncidentDTO dto) {
        incidentUpdateProcessor.onNext(dto);
    }

    @Outgoing("incident-reported")
    public Multi<Message<IncidentDTO>> createIncident() {
        return incidentCreateProcessor.onItem().transform(this::sendCreateIncident);
    }

    private Message<IncidentDTO> sendCreateIncident(IncidentDTO incident) {
        log.debug("IncidentReportedEvent: " + incident);
        return KafkaRecord.of(incident.getIncidentId(), incident)
                .addMetadata(OutgoingCloudEventMetadata.builder().withType("IncidentReportedEvent")
                        .withTimestamp(OffsetDateTime.now().toZonedDateTime()).build());
    }

    @Outgoing("incident-updated")
    public Multi<Message<IncidentDTO>> updateIncident() {
        return incidentUpdateProcessor.onItem().transform(this::sendUpdateIncident);
    }

    private Message<IncidentDTO> sendUpdateIncident(IncidentDTO incident) {
        log.debug("IncidentUpdatedEvent: " + incident);
        return KafkaRecord.of(incident.getIncidentId(), incident).addMetadata(OutgoingCloudEventMetadata.builder()
                .withType("IncidentUpdatedEvent").withTimestamp(OffsetDateTime.now().toZonedDateTime()).build());
    }
}
