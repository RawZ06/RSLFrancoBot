package fr.rawz06.rslfrancobot.engine.domain.ports;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;

import java.util.Optional;

/**
 * Port for accessing configuration presets.
 * Abstraction of preset storage (YAML, JSON, DB, etc.).
 * Implemented in the API Layer.
 */
public interface IPresetRepository {
    /**
     * Retrieves a preset by name.
     *
     * @param name Preset name (franco, rsl, pot)
     * @return The preset if found
     */
    Optional<Preset> getPreset(String name);

    /**
     * Checks if a preset exists.
     *
     * @param name Preset name
     * @return true if the preset exists
     */
    boolean presetExists(String name);
}
