package fr.rawz06.rslfrancobot.engine.usecases.s9;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.springframework.stereotype.Component;

/**
 * Use Case: Generates a seed in S9 mode.
 * Uses fixed settings from s9.json - no user configuration.
 */
@Component
public class GenerateS9SeedUseCase {

    private final IPresetRepository presetRepository;
    private final IRandomizerApi randomizerApi;

    public GenerateS9SeedUseCase(
            IPresetRepository presetRepository,
            IRandomizerApi randomizerApi
    ) {
        this.presetRepository = presetRepository;
        this.randomizerApi = randomizerApi;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Retrieve S9 preset (contains fixed settings from s9.json)
        Preset s9Preset = presetRepository.getPreset("s9")
                .orElseThrow(() -> new GenerationException("S9 preset not found"));

        // 2. Create SettingsFile from preset's base settings (already complete)
        SettingsFile settings = new SettingsFile(s9Preset.baseSettings());

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
