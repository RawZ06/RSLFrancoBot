package fr.rawz06.rslfrancobot.engine.domain.entities;

/**
 * Résultat de la génération d'une seed.
 * Contient les informations retournées par le randomizer.
 */
public record SeedResult(
        String seedUrl,
        String seedHash,
        SettingsFile usedSettings
) {
    public SeedResult {
        if (seedUrl == null || seedUrl.isBlank()) {
            throw new IllegalArgumentException("L'URL de la seed ne peut pas être vide");
        }
    }
}
