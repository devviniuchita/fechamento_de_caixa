package com.controle.fechamentocaixa.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.controle.fechamentocaixa.model.Perfil;

/**
 * Configuração do MongoDB com conversores customizados para enums
 */
@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new PerfilReadConverter());
        converters.add(new PerfilWriteConverter());
        return new MongoCustomConversions(converters);
    }

    /**
     * Converter para ler Perfil do MongoDB (String -> Perfil)
     */
    public static class PerfilReadConverter implements Converter<String, Perfil> {
        @Override
        public Perfil convert(String source) {
            if (source == null) {
                return Perfil.CAIXA;
            }
            try {
                return Perfil.valueOf(source);
            } catch (IllegalArgumentException e) {
                // Se não conseguir converter, retorna CAIXA como padrão
                return Perfil.CAIXA;
            }
        }
    }

    /**
     * Converter para escrever Perfil no MongoDB (Perfil -> String)
     */
    public static class PerfilWriteConverter implements Converter<Perfil, String> {
        @Override
        public String convert(Perfil source) {
            return source != null ? source.name() : "CAIXA";
        }
    }
}
