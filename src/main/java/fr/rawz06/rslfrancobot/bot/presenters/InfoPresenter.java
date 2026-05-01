package fr.rawz06.rslfrancobot.bot.presenters;

import fr.rawz06.rslfrancobot.bot.models.DiscordMessage;
import fr.rawz06.rslfrancobot.config.AppVersionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class InfoPresenter {

    private final AppVersionConfig versionConfig;
    private final String botVersion;

    public InfoPresenter(AppVersionConfig versionConfig, @Value("${app.version:unknown}") String botVersion) {
        this.versionConfig = versionConfig;
        this.botVersion = botVersion;
    }

    public DiscordMessage presentInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ℹ️ **Informations du Bot**\n\n");
        
        sb.append("**Versions système :**\n");
        sb.append("- Bot : `").append(botVersion).append("`\n");
        sb.append("- Spring Boot : `").append(SpringBootVersion.getVersion()).append("`\n");
        sb.append("- Java : `").append(System.getProperty("java.version")).append("`\n\n");
        
        sb.append("**Versions des Générateurs :**\n");
        sb.append("- RSL : `").append(versionConfig.getRsl()).append("`\n");
        sb.append("- RSL S8 : `").append(versionConfig.getRsl_s8()).append("`\n");
        sb.append("- S8 : `").append(versionConfig.getS8()).append("`\n");
        sb.append("- S9 : `").append(versionConfig.getS9()).append("`\n");
        sb.append("- Franco : `").append(versionConfig.getFranco()).append("`\n");
        sb.append("- ToT : `").append(versionConfig.getTot()).append("`\n");
        sb.append("- Enemy Salad : `").append(versionConfig.getEnemySalad()).append("`\n");
        sb.append("- Allsanity : `").append(versionConfig.getAllsanity()).append("`\n");
        sb.append("- Salad : `").append(versionConfig.getSalad()).append("`\n\n");
        
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long startTime = rb.getStartTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        String formattedStartTime = formatter.format(Instant.ofEpochMilli(startTime));
        
        sb.append("**Démarré le :** `").append(formattedStartTime).append("`\n");
        
        return new DiscordMessage(sb.toString());
    }
}
