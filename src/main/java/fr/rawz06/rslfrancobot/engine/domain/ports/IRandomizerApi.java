package fr.rawz06.rslfrancobot.engine.domain.ports;

import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
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
     * @param mode Seed mode (to determine API version)
     * @param settings Complete settings for generation
     * @return Result containing the URL and hash of the seed
     * @throws RandomizerApiException If generation fails
     */
    SeedResult generateSeed(SeedMode mode, SettingsFile settings) throws RandomizerApiException;

    class RandomizerApiException extends Exception {
        public RandomizerApiException(String message) {
            super(message);
        }

        public RandomizerApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
