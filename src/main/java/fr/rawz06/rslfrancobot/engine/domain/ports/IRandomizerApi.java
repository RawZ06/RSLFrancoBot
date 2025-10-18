package fr.rawz06.rslfrancobot.engine.domain.ports;

import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;

/**
 * Port pour l'API du randomizer OoT.
 * Abstraction de la communication avec ootrandomizer.com.
 * Implémenté dans l'API Layer.
 */
public interface IRandomizerApi {
    /**
     * Génère une seed sur le site ootrandomizer.com.
     *
     * @param settings Les settings complets pour la génération
     * @return Le résultat contenant l'URL et le hash de la seed
     * @throws RandomizerApiException Si la génération échoue
     */
    SeedResult generateSeed(SettingsFile settings) throws RandomizerApiException;

    class RandomizerApiException extends Exception {
        public RandomizerApiException(String message) {
            super(message);
        }

        public RandomizerApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
