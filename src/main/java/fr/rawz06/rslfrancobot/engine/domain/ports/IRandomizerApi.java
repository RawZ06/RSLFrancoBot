package fr.rawz06.rslfrancobot.engine.domain.ports;

import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;

/**
 * Port for the OoT randomizer API.
 * Abstraction of communication with ootrandomizer.com.
 * Implemented in the API Layer.
 */
public interface IRandomizerApi {
    /**
     * Generates a seed on ootrandomizer.com.
     *
     * @param settings Complete settings for generation
     * @return Result containing the URL and hash of the seed
     * @throws RandomizerApiException If generation fails
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
