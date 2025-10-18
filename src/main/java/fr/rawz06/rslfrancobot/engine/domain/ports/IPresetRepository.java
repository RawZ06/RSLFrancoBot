package fr.rawz06.rslfrancobot.engine.domain.ports;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;

import java.util.Optional;

/**
 * Port pour l'accès aux presets de configuration.
 * Abstraction du stockage des presets (YAML, JSON, BDD, etc.).
 * Implémenté dans l'API Layer.
 */
public interface IPresetRepository {
    /**
     * Récupère un preset par son nom.
     *
     * @param name Le nom du preset (franco, rsl, pot)
     * @return Le preset si trouvé
     */
    Optional<Preset> getPreset(String name);

    /**
     * Vérifie si un preset existe.
     *
     * @param name Le nom du preset
     * @return true si le preset existe
     */
    boolean presetExists(String name);
}
