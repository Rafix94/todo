package com.todolist.refinementservice.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<UserRepresentation> getUserById(String userId) {
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();

        UserRepresentation user = usersResource.get(userId).toRepresentation();
        return Optional.ofNullable(user);
    }

    public List<GroupRepresentation> getGroups() {
        RealmResource realmResource = keycloak.realm(realmName);
        GroupsResource groups = realmResource.groups();

        return groups.groups();
    }
}
