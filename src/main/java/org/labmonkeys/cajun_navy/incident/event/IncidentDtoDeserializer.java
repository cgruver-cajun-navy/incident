package org.labmonkeys.cajun_navy.incident.event;

import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class IncidentDtoDeserializer extends ObjectMapperDeserializer<IncidentDTO> {
    public IncidentDtoDeserializer() {
        super(IncidentDTO.class);
    }
}
