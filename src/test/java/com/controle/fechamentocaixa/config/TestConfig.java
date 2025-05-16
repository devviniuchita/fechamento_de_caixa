package com.controle.fechamentocaixa.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@TestConfiguration
public class TestConfig {
    
    private static final MongoDBContainer mongoDBContainer;
    
    static {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4"));
        mongoDBContainer.start();
    }
    
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    }
    
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "test_db");
    }
} 