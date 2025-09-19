package com.controle.fechamentocaixa;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.*;
import com.mongodb.client.*;

public class MongoClientConnectionExample {
  private static final Logger log = LoggerFactory.getLogger(MongoClientConnectionExample.class);

  public static void main(String[] args) {
    String connectionString = System.getenv("MONGODB_URI");
    if (connectionString == null || connectionString.isBlank()) {
      System.err.println("Erro: defina a variável de ambiente MONGODB_URI com sua connection string do MongoDB.");
      System.exit(1);
    }

    String databaseName = System.getenv("MONGODB_DB");
    if (databaseName == null || databaseName.isBlank()) {
      databaseName = "admin";
    }

    ServerApi serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build();

    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .serverApi(serverApi)
        .build();

    // Create a new client and connect to the server
    try (MongoClient mongoClient = MongoClients.create(settings)) {
      try {
        // Send a ping to confirm a successful connection
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        database.runCommand(new Document("ping", 1));
        log.info("Pinged MongoDB deployment. Connection successful to database: {}", databaseName);
      } catch (MongoException e) {
        log.error("Erro ao conectar ou pingar o MongoDB: {}", e.getMessage(), e);
      }
    }
  }
}
