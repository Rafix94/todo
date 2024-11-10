package com.recruitment.useragent.service;

import com.recruitment.useragent.dto.TeamDetailsDto;
import com.recruitment.useragent.dto.TeamMemberDto;
import com.recruitment.useragent.dto.TeamSummaryDto;
import com.recruitment.useragent.mapper.TeamMapper;
import com.recruitment.useragent.model.MembershipStatus;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final String realmName;

    @Autowired
    public KeycloakUserService(Keycloak keycloak,
                               @Value("${keycloak.realm}") String realmName) {
        this.keycloak = keycloak;
        this.realmName = realmName;
    }

    private List<GroupRepresentation> listGroups() {
        GroupsResource groupsResource = keycloak.realm(realmName).groups();
        return new ArrayList<>(groupsResource.groups());
    }

    public List<TeamSummaryDto> getAllTeamsWithMembershipStatus(MembershipStatus membershipStatus) {
        String uuid = SecurityContextHolder.getContext().getAuthentication().getName();
        List<GroupRepresentation> groups = listGroups();
        UserResource userResource = keycloak.realm(realmName).users().get(uuid);

        List<GroupRepresentation> userGroups = userResource.groups();

        return groups.stream()
                .filter(group -> switch (membershipStatus) {
                    case MEMBER -> userGroups.stream().anyMatch(userGroup -> userGroup.getId().equals(group.getId()));
                    case NOT_MEMBER -> userGroups.stream().noneMatch(userGroup -> userGroup.getId().equals(group.getId()));
                    case ALL -> true;
                })
                .map(group -> {
                    boolean isMember = userGroups.stream().anyMatch(userGroup -> userGroup.getId().equals(group.getId()));
                    return new TeamSummaryDto(group.getId(), group.getName(), isMember);
                })
                .collect(Collectors.toList());
    }

    public TeamDetailsDto createGroup(TeamDetailsDto teamDetailsDto) {
        GroupsResource groupsResource = keycloak.realm(realmName).groups();
        GroupRepresentation group = new GroupRepresentation();
        group.setName(teamDetailsDto.name());

        Response response = groupsResource.add(group);
        if (response.getStatus() == 201) {
            String location = response.getHeaderString("Location");
            if (location != null) {
                String groupId = location.substring(location.lastIndexOf("/") + 1);

                GroupRepresentation createdGroup = groupsResource.group(groupId).toRepresentation();
                return TeamMapper.mapToTeamDto(createdGroup);
            } else {
                throw new RuntimeException("Failed to retrieve the Location header from the response.");
            }
        } else {
            throw new RuntimeException("Failed to create team in Keycloak: " + response.getStatusInfo());
        }
    }

    public void joinGroup(String groupId) {
        String uuid = SecurityContextHolder.getContext().getAuthentication().getName();
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();

        GroupRepresentation groupRepresentation = realmResource.groups().group(groupId).toRepresentation();
        if (groupRepresentation == null) {
            throw new IllegalArgumentException("Group not found for ID: " + groupId);
        }

        List<GroupRepresentation> userGroups = usersResource.get(uuid).groups();
        boolean isAlreadyMember = userGroups.stream()
                .anyMatch(group -> group.getId().equals(groupId));

        if (isAlreadyMember) {
            return;
        }

        usersResource.get(uuid).joinGroup(groupId);
    }

    public TeamDetailsDto getTeamDetails(String groupId) {
        RealmResource realmResource = keycloak.realm(realmName);
        GroupResource groupResource = realmResource.groups().group(groupId);

        List<UserRepresentation> userRepresentations = groupResource.members();


        List<TeamMemberDto> teamMembers = userRepresentations.stream().map(user ->
                new TeamMemberDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                )).collect(Collectors.toList());

        return new TeamDetailsDto(groupResource.toRepresentation().getId(),
                groupResource.toRepresentation().getName(),
                teamMembers);

    }
}
