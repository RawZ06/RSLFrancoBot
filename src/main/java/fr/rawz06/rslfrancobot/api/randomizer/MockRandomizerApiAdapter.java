package fr.rawz06.rslfrancobot.api.randomizer;

import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implémentation Mock de l'API Randomizer.
 * Simule la génération de seed sans appeler le vrai site.
 * À remplacer par une vraie implémentation HTTP plus tard.
 */
@Component
public class MockRandomizerApiAdapter implements IRandomizerApi {

    @Override
    public SeedResult generateSeed(SettingsFile settings) throws RandomizerApiException {
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
