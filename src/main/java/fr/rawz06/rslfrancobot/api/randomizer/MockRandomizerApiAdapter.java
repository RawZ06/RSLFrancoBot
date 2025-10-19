package fr.rawz06.rslfrancobot.api.randomizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mock implementation of the Randomizer API.
 * Simulates seed generation without calling the real site.
 * To be replaced with real HTTP implementation later.
 */
@Component
public class MockRandomizerApiAdapter implements IRandomizerApi {

    private static final Logger logger = LoggerFactory.getLogger(MockRandomizerApiAdapter.class);
    private final ObjectMapper objectMapper;

    public MockRandomizerApiAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SeedResult generateSeed(SeedMode mode, SettingsFile settings) throws RandomizerApiException {
        // Display settings in formatted JSON for verification
        try {
            String jsonSettings = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(settings.settings());

            System.out.println("\n" + "=".repeat(80));
            System.out.println("📋 SETTINGS SENT TO RANDOMIZER API (MOCK)");
            System.out.println("=".repeat(80));
            System.out.println(jsonSettings);
            System.out.println("=".repeat(80) + "\n");

            logger.info("Settings contains {} keys", settings.settings().size());
        } catch (Exception e) {
            logger.error("Error serializing settings", e);
        }

        // Simulate realistic network delay (5 seconds like a real HTTP call)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RandomizerApiException("Generation interrupted", e);
        }

        // Generate mock result
        String mockId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String mockUrl = "https://ootrandomizer.com/seed/get?id=" + mockId;
        String mockVersion = "8.3.0";
        Boolean mockSpoilers = true;

        return new SeedResult(mockUrl, mockVersion, mockSpoilers, settings);
    }
}
