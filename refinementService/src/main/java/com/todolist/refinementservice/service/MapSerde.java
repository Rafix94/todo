package com.todolist.refinementservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import com.todolist.refinementservice.model.UserVoteState;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.util.Map;
import java.util.UUID;

public class MapSerde {
    public static Serde<Map<UUID, UserVoteState>> userVoteStateMapSerde() {

        JsonSerializer<Map<UUID, UserVoteState>> serializer = new JsonSerializer<>();
        JsonDeserializer<Map<UUID, UserVoteState>> deserializer = new JsonDeserializer<>(
                new TypeReference<>() {}
        );

        deserializer.addTrustedPackages("*");

        return Serdes.serdeFrom(serializer, deserializer);
    }
}
