package org.labmonkeys.cajun_navy.incident.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
import org.labmonkeys.cajun_navy.incident.dto.VictimDTO;
import org.labmonkeys.cajun_navy.incident.model.Incident;
import org.labmonkeys.cajun_navy.incident.model.Victim;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface IncidentMapper {
    
    @Mapping(target = "id", ignore = true)
    Incident incidentDtoToEntity(IncidentDTO dto);
    @Mapping(target = "id", ignore = true)
    Victim victimDtoToEntity(VictimDTO dto);
    IncidentDTO incidentEntityToDto(Incident entity);
    VictimDTO victimEntityToDto(Victim entity);

    List<Incident> incidentDtosToEntities(List<IncidentDTO> dtos);
    List<Victim> victimDtosToEntities(List<VictimDTO> dtos);
    List<IncidentDTO> incidentEntitiesToDtos(List<Incident> entities);
    List<VictimDTO> victimEntitiesToDtos(List<Victim> entities);

    @AfterMapping
    default void incidentDtoToEntityCustom(IncidentDTO dto, @MappingTarget Incident entity) {
        entity.setLatitude(BigDecimal.valueOf(dto.getLatitude()).setScale(5, RoundingMode.HALF_UP).toString());
        entity.setLongitude(BigDecimal.valueOf(dto.getLongitude()).setScale(5, RoundingMode.HALF_UP).toString());
        for (Victim victim : entity.getVictims()) {
            victim.setIncident(entity);
        }
    }

    @AfterMapping
    default void incidentEntityToDtoCustom(Incident entity, @MappingTarget IncidentDTO dto) {
        dto.setLatitude(new BigDecimal(entity.getLatitude()).doubleValue());
        dto.setLongitude(new BigDecimal(entity.getLongitude()).doubleValue());
    }
}
