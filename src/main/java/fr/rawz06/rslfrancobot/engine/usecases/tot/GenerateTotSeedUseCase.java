package fr.rawz06.rslfrancobot.engine.usecases.tot;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedRequest;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.springframework.stereotype.Component;

/**
 * Use Case: Generates a seed in S9 mode.
 * Uses fixed settings from s9.json - no user configuration.
 */
@Component
public class GenerateTotSeedUseCase {

    private final IPresetRepository presetRepository;
    private final IRandomizerApi randomizerApi;

    public GenerateTotSeedUseCase(
            IPresetRepository presetRepository,
            IRandomizerApi randomizerApi
    ) {
        this.presetRepository = presetRepository;
        this.randomizerApi = randomizerApi;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Retrieve S9 preset (contains fixed settings from tot.json)
        Preset totPreset = presetRepository.getPreset("tot")
                .orElseThrow(() -> new GenerationException("ToT preset not found"));

        // 2. Create SettingsFile from preset's base settings (already complete)
        SettingsFile settings = new SettingsFile(totPreset.baseSettings());

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
