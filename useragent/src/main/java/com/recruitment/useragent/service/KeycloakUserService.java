package com.recruitment.useragent.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.core.Response;

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

    public void registerUser(String username, String email, String password, List<String> roleNames) {
        UsersResource usersResource = keycloak.realm(realmName).users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        user.setCredentials(Collections.singletonList(credential));

        try (Response response = usersResource.create(user)) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            assignRolesToUser(userId, roleNames);
        }
    }

    private void assignRolesToUser(String userId, List<String> roleNames) {
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();

        roleNames.stream()
                .map(role -> realmResource.roles().get(role).toRepresentation())
                .forEach(role -> usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role)));
    }

    public List<GroupRepresentation> listGroups() {
        GroupsResource groupsResource = keycloak.realm(realmName).groups();
        return groupsResource.groups().stream().collect(Collectors.toList());
    }

    public GroupRepresentation createGroup(String groupName) {
        GroupsResource groupsResource = keycloak.realm(realmName).groups();
        GroupRepresentation group = new GroupRepresentation();
        group.setName(groupName);

        Response response = groupsResource.add(group);
        if (response.getStatus() == 201) {
            return response.readEntity(GroupRepresentation.class);
        } else {
            throw new RuntimeException("Failed to create group in Keycloak: " + response.getStatusInfo());
        }
    }

    public void joinGroup(String userId, String groupId) {
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();

        GroupRepresentation representation = realmResource.groups().group(groupId).toRepresentation();
        usersResource.get(userId).groups().add(representation);

    }
}
