package com.todolist.refinementservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import com.todolist.refinementservice.dto.UserVoteStateDTO;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.util.Map;
import java.util.UUID;

public class MapSerde {
    public static Serde<Map<UUID, UserVoteStateDTO>> userVoteStateMapSerde() {

        JsonSerializer<Map<UUID, UserVoteStateDTO>> serializer = new JsonSerializer<>();
        JsonDeserializer<Map<UUID, UserVoteStateDTO>> deserializer = new JsonDeserializer<>(
                new TypeReference<>() {}
        );

        deserializer.addTrustedPackages("*");

        return Serdes.serdeFrom(serializer, deserializer);
    }
}
