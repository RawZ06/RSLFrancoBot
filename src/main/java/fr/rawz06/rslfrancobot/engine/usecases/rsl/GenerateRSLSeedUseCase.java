package fr.rawz06.rslfrancobot.engine.usecases.rsl;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRSLScriptRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Use Case: Generates a seed in RSL, PoT, or Beginner mode.
 * Uses the Python script to generate random settings, then generates the seed.
 */
@Component
public class GenerateRSLSeedUseCase {

    private final IPresetRepository presetRepository;
    private final IRSLScriptRunner rslScriptRunner;
    private final IRandomizerApi randomizerApi;

    public GenerateRSLSeedUseCase(
            IPresetRepository presetRepository,
            IRSLScriptRunner rslScriptRunner,
            IRandomizerApi randomizerApi
    ) {
        this.presetRepository = presetRepository;
        this.rslScriptRunner = rslScriptRunner;
        this.randomizerApi = randomizerApi;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // Determine preset name based on mode
        String presetName = switch (request.mode()) {
            case RSL -> "rsl";
            case POT -> "pot";
            case BEGINNER -> "beginner";
            default -> throw new GenerationException("Unsupported mode for RSL/PoT/Beginner: " + request.mode());
        };

        // 1. Retrieve preset
        Preset preset = presetRepository.getPreset(presetName)
                .orElseThrow(() -> new GenerationException("Preset " + presetName + " not found"));

        // 2. Generate settings via Python script
        SettingsFile generatedSettings;
        try {
            generatedSettings = rslScriptRunner.generateSettings(preset);
        } catch (IRSLScriptRunner.ScriptExecutionException e) {
            throw new GenerationException("Error executing RSL script", e);
        }

        // 2.5. Add hardcoded settings for RSL modes
        Map<String, Object> modifiedSettings = new HashMap<>(generatedSettings.settings());
        modifiedSettings.put("show_seed_info", true);
        modifiedSettings.put("create_spoiler", true);
        modifiedSettings.put("password_lock", false);
        SettingsFile finalSettings = new SettingsFile(modifiedSettings);

        // 3. Generate seed via API
        try {
            return randomizerApi.generateSeed(request.mode(), finalSettings);
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
