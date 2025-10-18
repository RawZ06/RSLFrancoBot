package fr.rawz06.rslfrancobot.engine.domain.entities;

import java.util.Map;

/**
 * Represents a complete settings file for the randomizer.
 * Contains all the configuration needed to generate a seed.
 */
public record SettingsFile(
        Map<String, Object> settings
) {
    public SettingsFile {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
    }

    public Object getSetting(String key) {
        return settings.get(key);
    }

    public boolean hasSetting(String key) {
        return settings.containsKey(key);
    }
}
