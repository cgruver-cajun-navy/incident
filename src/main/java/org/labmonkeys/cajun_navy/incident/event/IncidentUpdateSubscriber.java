package org.labmonkeys.cajun_navy.incident.event;

import java.time.OffsetDateTime;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
import org.labmonkeys.cajun_navy.incident.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class IncidentUpdateSubscriber {

    private final static Logger log = LoggerFactory.getLogger(IncidentUpdateSubscriber.class);

    private final UnicastProcessor<IncidentDTO> processor = UnicastProcessor.create();

    @Inject
    IncidentService service;

    @Incoming("update-incident-location")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public void updateIncidentLocation(Message<IncidentDTO> message) {
        IncidentDTO incident = service.updateIncidentLocation(message.getPayload());
        if (incident != null) {
            processor.onNext(incident);
        }
        message.ack();
    }

    @Incoming("update-incident-status")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public void updateIncidentStatus(Message<IncidentDTO> message) {
        IncidentDTO incident = service.updateIncidentStatus(message.getPayload());
        if (incident != null) {
            processor.onNext(incident);
        }
        message.ack();
    }

    @Outgoing("incident-update-event")
    public Multi<Message<IncidentDTO>> source() {
        return processor.onItem().transform(this::toMessage);
    }

    private Message<IncidentDTO> toMessage(IncidentDTO incident) {
        log.debug("IncidentUpdatedEvent: " + incident);
        return KafkaRecord.of(incident.getIncidentId(), incident)
                .addMetadata(OutgoingCloudEventMetadata.builder().withType("IncidentUpdatedEvent")
                        .withTimestamp(OffsetDateTime.now().toZonedDateTime()).build());
    }
}