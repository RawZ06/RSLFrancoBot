package fr.rawz06.rslfrancobot.engine.usecases.s8;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.springframework.stereotype.Component;

/**
 * Use Case: Generates a seed in S8 (Standard) mode.
 * Uses fixed settings from standard.json - no user configuration.
 */
@Component
public class GenerateS8SeedUseCase {

    private final IPresetRepository presetRepository;
    private final IRandomizerApi randomizerApi;

    public GenerateS8SeedUseCase(
            IPresetRepository presetRepository,
            IRandomizerApi randomizerApi
    ) {
        this.presetRepository = presetRepository;
        this.randomizerApi = randomizerApi;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Retrieve S8 preset (contains fixed settings from standard.json)
        Preset s8Preset = presetRepository.getPreset("s8")
                .orElseThrow(() -> new GenerationException("S8 preset not found"));

        // 2. Create SettingsFile from preset's base settings (already complete)
        SettingsFile settings = new SettingsFile(s8Preset.baseSettings());

        // 3. Generate seed via API
        try {
            return randomizerApi.generateSeed(request.mode(), settings);
        } catch (IRandomizerApi.RandomizerApiException e) {
            throw new GenerationException("Error during seed generation", e);
        }
    }

    public static class GenerationException extends Exception {
        public GenerationException(String message) {
            super(message);
        }

        public GenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
