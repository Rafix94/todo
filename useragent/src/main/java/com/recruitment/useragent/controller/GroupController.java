package com.recruitment.useragent.controller;

import com.recruitment.useragent.service.KeycloakUserService;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final KeycloakUserService keycloakUserService;

    @Autowired
    public GroupController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @GetMapping
    public ResponseEntity<List<GroupRepresentation>> getAllGroups() {
        List<GroupRepresentation> groups = keycloakUserService.listGroups();
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<GroupRepresentation> createGroup(@RequestBody String groupName) {
        GroupRepresentation group = keycloakUserService.createGroup(groupName);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @PostMapping("/join/{userId}/{groupId}")
    public ResponseEntity<Void> joinGroup(@PathVariable String userId, @PathVariable String groupId) {
        keycloakUserService.joinGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }
}
