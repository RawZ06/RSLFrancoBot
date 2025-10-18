package fr.rawz06.rslfrancobot.engine.domain.entities;

import java.util.Map;

/**
 * Représente une demande de génération de seed.
 * Entité du domaine métier.
 */
public record SeedRequest(
        SeedMode mode,
        String userId,
        Map<String, String> userSettings
) {
    public SeedRequest {
        if (mode == null) {
            throw new IllegalArgumentException("Le mode ne peut pas être null");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("L'userId ne peut pas être vide");
        }
    }
}
