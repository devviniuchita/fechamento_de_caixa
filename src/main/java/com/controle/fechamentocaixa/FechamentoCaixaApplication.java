package com.controle.fechamentocaixa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Classe principal da aplicação de Fechamento de Caixa
 * Inicializa o Spring Boot e todas as configurações
 */
@SpringBootApplication
@EnableMongoRepositories
public class FechamentoCaixaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FechamentoCaixaApplication.class, args);
    }
}
