package fr.rawz06.rslfrancobot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.randomizer.api.version")
public class AppVersionConfig {
    private String rsl;
    private String rsl_s8;
    private String s8;
    private String s9;
    private String franco;
    private String tot;
    private String mixed;
    private String enemySalad;
    private String allsanity;
    private String salad;
}
