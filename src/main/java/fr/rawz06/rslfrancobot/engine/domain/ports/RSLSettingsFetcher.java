package fr.rawz06.rslfrancobot.engine.domain.ports;

import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;

/**
 * Port for fetching RSL settings from an external API.
 */
public interface RSLSettingsFetcher {
    /**
     * Fetches settings from the external API for a specific season.
     *
     * @param season The season identifier (e.g., "rsl_season8")
     * @return The settings for the season
     * @throws SettingsFetcherException If fetching fails
     */
    SettingsFile fetchSettings(String season) throws SettingsFetcherException;

    class SettingsFetcherException extends Exception {
        public SettingsFetcherException(String message) {
            super(message);
        }

        public SettingsFetcherException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
