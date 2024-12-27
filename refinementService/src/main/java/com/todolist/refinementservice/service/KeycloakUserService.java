package com.todolist.refinementservice.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @Cacheable("userData")
    public Optional<UserRepresentation> getUserById(String userId) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = usersResource.get(userId).toRepresentation();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            System.err.println("Error fetching user with ID " + userId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
