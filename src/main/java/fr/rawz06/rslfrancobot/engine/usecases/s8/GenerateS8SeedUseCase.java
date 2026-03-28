package fr.rawz06.rslfrancobot.engine.usecases.s8;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.PresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.RandomizerApi;
import org.springframework.stereotype.Component;

/**
 * Use Case: Generates a seed in S8 mode.
 * Uses fixed settings from s8.json - no user configuration.
 */
@Component
public class GenerateS8SeedUseCase {

    private final PresetRepository presetRepository;
    private final RandomizerApi randomizerApi;

    public GenerateS8SeedUseCase(
            PresetRepository presetRepository,
            RandomizerApi randomizerApi
    ) {
        this.presetRepository = presetRepository;
        this.randomizerApi = randomizerApi;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Retrieve S8 preset (contains fixed settings from s8.json)
        Preset s8Preset = presetRepository.getPreset("s8")
                .orElseThrow(() -> new GenerationException("S8 preset not found"));

        // 2. Create SettingsFile from preset's base settings (already complete)
        SettingsFile settings = new SettingsFile(s8Preset.baseSettings());

        // 3. Generate seed via API
        try {
            return randomizerApi.generateSeed(request.mode(), settings);
        } catch (RandomizerApi.RandomizerApiException e) {
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
