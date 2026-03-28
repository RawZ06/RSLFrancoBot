package fr.rawz06.rslfrancobot.api.external;

import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.RSLSettingsFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Implementation of IRSLSettingsFetcher using RestTemplate to call a remote API.
 */
@Component
public class RSLSettingsFetcherImpl implements RSLSettingsFetcher {

    private static final Logger logger = LoggerFactory.getLogger(RSLSettingsFetcherImpl.class);

    private final RestTemplate restTemplate;

    @Value("${app.rsl.api.base-url}")
    private String apiBaseUrl;

    public RSLSettingsFetcherImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public SettingsFile fetchSettings(String season) throws SettingsFetcherException {
        String url = apiBaseUrl + "/" + season;
        logger.info("Fetching RSL settings from: {}", url);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("settings")) {
                throw new SettingsFetcherException("Invalid response from RSL API: 'settings' property missing");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> settings = (Map<String, Object>) response.get("settings");
            return new SettingsFile(settings);

        } catch (Exception e) {
            logger.error("Error fetching RSL settings from API", e);
            throw new SettingsFetcherException("Failed to fetch RSL settings: " + e.getMessage(), e);
        }
    }
}
