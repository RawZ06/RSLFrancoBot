package fr.rawz06.rslfrancobot.engine.usecases.mixed;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedRequest;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.PresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.RandomizerApi;
import org.springframework.stereotype.Component;

/**
 * Use Case: Generates a seed in Mixed mode.
 * Uses fixed settings from mixed.json - no user configuration.
 */
@Component
public class GenerateMixedSeedUseCase {

    private final PresetRepository presetRepository;
    private final RandomizerApi randomizerApi;

    public GenerateMixedSeedUseCase(
            PresetRepository presetRepository,
            RandomizerApi randomizerApi
    ) {
        this.presetRepository = presetRepository;
        this.randomizerApi = randomizerApi;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Retrieve Mixed preset (contains fixed settings from mixed.json)
        Preset mixedPreset = presetRepository.getPreset("mixed")
                .orElseThrow(() -> new GenerationException("Mixed preset not found"));

        // 2. Create SettingsFile from preset's base settings (already complete)
        SettingsFile settings = new SettingsFile(mixedPreset.baseSettings());

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
