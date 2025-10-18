package fr.rawz06.rslfrancobot.engine.domain.entities;

import java.util.Map;

/**
 * Represents a seed generation request.
 * Business domain entity.
 */
public record SeedRequest(
        SeedMode mode,
        String userId,
        Map<String, String> userSettings
) {
    public SeedRequest {
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be empty");
        }
    }
}
