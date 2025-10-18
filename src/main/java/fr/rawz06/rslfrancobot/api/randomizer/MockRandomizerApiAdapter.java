package fr.rawz06.rslfrancobot.api.randomizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implémentation Mock de l'API Randomizer.
 * Simule la génération de seed sans appeler le vrai site.
 * À remplacer par une vraie implémentation HTTP plus tard.
 */
@Component
public class MockRandomizerApiAdapter implements IRandomizerApi {

    private static final Logger logger = LoggerFactory.getLogger(MockRandomizerApiAdapter.class);
    private final ObjectMapper objectMapper;

    public MockRandomizerApiAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SeedResult generateSeed(SettingsFile settings) throws RandomizerApiException {
        // Afficher les settings en JSON formaté pour vérification
        try {
            String jsonSettings = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(settings.settings());

            System.out.println("\n" + "=".repeat(80));
            System.out.println("📋 SETTINGS ENVOYÉS À L'API RANDOMIZER (MOCK)");
            System.out.println("=".repeat(80));
            System.out.println(jsonSettings);
            System.out.println("=".repeat(80) + "\n");

            logger.info("Settings contient {} clés", settings.settings().size());
        } catch (Exception e) {
            logger.error("Erreur lors de la sérialisation des settings", e);
        }

        // Simuler un délai réseau réaliste (5 secondes comme un vrai appel HTTP)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RandomizerApiException("Interruption pendant la génération", e);
        }

        // Générer un résultat fictif
        String mockHash = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String mockUrl = "https://ootrandomizer.com/seed/get?id=" + mockHash;

        return new SeedResult(mockUrl, mockHash, settings);
    }
}
