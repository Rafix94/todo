package com.todolist.refinementservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import com.todolist.refinementservice.model.UserVoteState;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.util.Map;
import java.util.UUID;

public class MapSerde {
    public static Serde<Map<UUID, UserVoteState>> userVoteStateMapSerde() {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonSerializer<Map<UUID, UserVoteState>> serializer = new JsonSerializer<>();
        JsonDeserializer<Map<UUID, UserVoteState>> deserializer = new JsonDeserializer<>(
                new TypeReference<Map<UUID, UserVoteState>>() {}
        );

        // Add trusted packages if necessary
        deserializer.addTrustedPackages("*");

        // Configure the serde
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
