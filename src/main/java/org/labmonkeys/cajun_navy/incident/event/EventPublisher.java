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
public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventBusSubscriber.class);

    private final UnicastProcessor<IncidentDTO> processor = UnicastProcessor.create();

    public void reportIncident(IncidentDTO dto) {
        processor.onNext(dto);
    }
    @Outgoing("incident-reported-event")
    public Multi<Message<IncidentDTO>> source() {
        return processor.onItem().transform(this::toMessage);
    }

    private Message<IncidentDTO> toMessage(IncidentDTO incident) {
        log.debug("IncidentReportedEvent: " + incident);
        return KafkaRecord.of(incident.getIncidentId(), incident)
                .addMetadata(OutgoingCloudEventMetadata.builder().withType("IncidentReportedEvent")
                        .withTimestamp(OffsetDateTime.now().toZonedDateTime()).build());
    }
}
