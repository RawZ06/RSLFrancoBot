package fr.rawz06.rslfrancobot.engine.domain.entities;

/**
 * Result of seed generation.
 * Contains information returned by the randomizer.
 */
public record SeedResult(
        String seedUrl,
        String version,
        Boolean spoilers,
        SettingsFile usedSettings
) {
    public SeedResult {
        if (seedUrl == null || seedUrl.isBlank()) {
            throw new IllegalArgumentException("Seed URL cannot be empty");
        }
    }
}
