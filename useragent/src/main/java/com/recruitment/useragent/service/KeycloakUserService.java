package com.recruitment.useragent.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

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

        // Create a new user representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        // Set user credentials
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        user.setCredentials(Collections.singletonList(credential));

        // Create the user in Keycloak
        Response response = usersResource.create(user);

        if (response.getStatus() == 201) {
            // Extract the user ID from the location header
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            // Assign roles to the user
            assignRolesToUser(userId, roleNames);
        } else {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
        }
    }

    private void assignRolesToUser(String userId, List<String> roleNames) {
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();

        roleNames.stream().map(role -> realmResource.roles().get(role).toRepresentation())
                .forEach(role -> usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role)));
    }
}
