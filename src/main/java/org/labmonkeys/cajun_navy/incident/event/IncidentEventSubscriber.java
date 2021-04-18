package org.labmonkeys.cajun_navy.incident.event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
import org.labmonkeys.cajun_navy.incident.service.IncidentService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@ApplicationScoped
public class IncidentEventSubscriber {

    //private final static Logger log = LoggerFactory.getLogger(IncidentUpdateSubscriber.class);

    @Inject
    IncidentService service;

    @Incoming("incident-update-location")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<CompletionStage<Void>> updateIncidentLocation(Message<IncidentDTO> message) {
        return CompletableFuture.supplyAsync(() -> {
            service.updateIncidentLocation(message.getPayload());
            return message.ack();
        });
    }

    @Incoming("incident-update-status")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<CompletionStage<Void>> updateIncidentStatus(Message<IncidentDTO> message) {
        return CompletableFuture.supplyAsync(() -> {
            service.updateIncidentStatus(message.getPayload());
            return message.ack();
        });
    }

    @Incoming("incident-update-priority")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<CompletionStage<Void>> updateIncidentPriority(Message<IncidentDTO> message) {
        return CompletableFuture.supplyAsync(() -> {
            service.updateIncidentPriority(message.getPayload());
            return message.ack();
        });
    }

    @Incoming("incident-assigned")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<CompletionStage<Void>> incidentAssignment() {
        return null;
    }

    @Incoming("incident-reported")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<CompletionStage<Void>> incidentReported() {
        return null;
    }

    @Incoming("priority-zone-clear")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<CompletionStage<Void>> PriorityZoneClear() {
        return null;
    }
}