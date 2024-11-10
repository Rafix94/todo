package com.recruitment.useragent.mapper;

import com.recruitment.useragent.dto.TeamDetailsDto;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.ArrayList;

public interface TeamMapper {
    static TeamDetailsDto mapToTeamDto(GroupRepresentation groupRepresentation) {
        return new TeamDetailsDto(
                groupRepresentation.getId(),
                groupRepresentation.getName(),
                new ArrayList<>());
    }
}
