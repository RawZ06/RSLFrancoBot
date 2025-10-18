package fr.rawz06.rslfrancobot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring pour Jackson ObjectMapper.
 * Fournit un bean ObjectMapper pour la sérialisation/désérialisation JSON.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
