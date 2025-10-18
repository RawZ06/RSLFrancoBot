package fr.rawz06.rslfrancobot.engine.domain.entities;

import java.util.List;
import java.util.Map;

/**
 * Représente un preset de configuration.
 * Contient les settings de base et les options configurables par l'utilisateur.
 */
public record Preset(
        String name,
        Map<String, Object> baseSettings,
        List<PresetOption> availableOptions
) {
    public Preset {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du preset ne peut pas être vide");
        }
        if (baseSettings == null) {
            throw new IllegalArgumentException("Les settings de base ne peuvent pas être null");
        }
    }

    /**
     * Option configurable dans un preset.
     */
    public record PresetOption(
            String id,
            String label,
            String description,
            Map<String, Object> settingsToApply,
            List<String> incompatibleWith
    ) {
        public PresetOption {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("L'ID de l'option ne peut pas être vide");
            }
            if (label == null || label.isBlank()) {
                throw new IllegalArgumentException("Le label ne peut pas être vide");
            }
        }
    }
}
