package fr.rawz06.rslfrancobot.api.randomizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Real HTTP implementation of the Randomizer API.
 * Calls https://ootrandomizer.com/api/v2/seed/create
 */
@Component
@Primary  // This will replace the Mock
public class HttpRandomizerApiAdapter implements IRandomizerApi {

    private static final Logger logger = LoggerFactory.getLogger(HttpRandomizerApiAdapter.class);

    @Value("${app.randomizer.api.url}")
    private String apiUrl;

    @Value("${app.randomizer.api.key}")
    private String apiKey;

    @Value("${app.randomizer.api.version.rsl}")
    private String versionRsl;

    @Value("${app.randomizer.api.version.pot}")
    private String versionPot;

    @Value("${app.randomizer.api.version.beginner}")
    private String versionBeginner;

    @Value("${app.randomizer.api.version.standard}")
    private String versionStandard;

    @Value("${app.randomizer.api.version.franco}")
    private String versionFranco;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public HttpRandomizerApiAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public SeedResult generateSeed(SeedMode mode, SettingsFile settings) throws RandomizerApiException {
        // Determine version based on seed mode
        String version = switch (mode) {
            case FRANCO -> versionFranco;
            case RSL -> versionRsl;
            case POT -> versionPot;
            case BEGINNER -> versionBeginner;
            case S8 -> versionStandard;
        };

        try {
            // Log settings being sent
            String jsonSettings = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(settings.settings());
            logger.info("Sending settings to randomizer API for mode {}, version {}:\n{}", mode, version, jsonSettings);

            // Build URL with query parameters
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("key", apiKey)
                    .queryParam("version", version)
                    .toUriString();

            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(settings.settings(), headers);

            // Send POST request
            logger.info("Calling API: POST {}", url);
            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ApiResponse.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RandomizerApiException("API returned status: " + response.getStatusCode());
            }

            ApiResponse apiResponse = response.getBody();
            logger.info("Seed created successfully: id={}, version={}, spoilers={}",
                    apiResponse.id, apiResponse.version, apiResponse.spoilers);

            // Build seed URL
            String seedUrl = "https://ootrandomizer.com/seed/get?id=" + apiResponse.id;

            return new SeedResult(
                    seedUrl,
                    apiResponse.version,
                    apiResponse.spoilers,
                    settings
            );

        } catch (Exception e) {
            logger.error("Error calling randomizer API", e);
            throw new RandomizerApiException("Failed to generate seed: " + e.getMessage(), e);
        }
    }

    /**
     * Response DTO from the randomizer API
     */
    private static class ApiResponse {
        public String id;
        public String version;
        public Boolean spoilers;
    }
}
