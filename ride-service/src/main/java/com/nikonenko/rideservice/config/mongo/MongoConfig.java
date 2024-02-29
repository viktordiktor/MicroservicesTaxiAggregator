package com.nikonenko.rideservice.config.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import lombok.RequiredArgsConstructor;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final MongodbProperties properties;

    @Bean
    public MongoClientSettings mongoClient() {
        return MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(getServerAddress()))
                .codecRegistry(getCodecRegistry())
                .build();
    }

    private CodecRegistry getCodecRegistry() {
        return fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    private List<ServerAddress> getServerAddress() {
        return Collections.singletonList(new ServerAddress(properties.getHost(), properties.getPort()));
    }
}