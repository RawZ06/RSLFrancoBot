package fr.rawz06.rslfrancobot.engine.domain.entities;

import java.util.Map;

/**
 * Représente un fichier de settings complet pour le randomizer.
 * Contient toute la configuration nécessaire pour générer une seed.
 */
public record SettingsFile(
        Map<String, Object> settings
) {
    public SettingsFile {
        if (settings == null) {
            throw new IllegalArgumentException("Les settings ne peuvent pas être null");
        }
    }

    public Object getSetting(String key) {
        return settings.get(key);
    }

    public boolean hasSetting(String key) {
        return settings.containsKey(key);
    }
}
