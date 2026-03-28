package fr.rawz06.rslfrancobot.engine.usecases.franco;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.PresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.RandomizerApi;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use Case: Generates a seed in Franco mode.
 * Orchestrates validation, settings construction, and seed generation.
 */
@Component
public class GenerateFrancoSeedUseCase {

    private final PresetRepository presetRepository;
    private final RandomizerApi randomizerApi;
    private final ValidateSettingsUseCase validateSettingsUseCase;
    private final BuildFinalSettingsUseCase buildFinalSettingsUseCase;

    public GenerateFrancoSeedUseCase(
            PresetRepository presetRepository,
            RandomizerApi randomizerApi,
            ValidateSettingsUseCase validateSettingsUseCase,
            BuildFinalSettingsUseCase buildFinalSettingsUseCase
    ) {
        this.presetRepository = presetRepository;
        this.randomizerApi = randomizerApi;
        this.validateSettingsUseCase = validateSettingsUseCase;
        this.buildFinalSettingsUseCase = buildFinalSettingsUseCase;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Retrieve Franco preset
        Preset francoPreset = presetRepository.getPreset("franco")
                .orElseThrow(() -> new GenerationException("Franco preset not found"));

        // 2. Extract selected option IDs
        List<String> selectedOptionIds = request.userSettings() != null
                ? List.copyOf(request.userSettings().keySet())
                : List.of();

        // 3. Validate settings
        ValidationResult validationResult = validateSettingsUseCase.execute(francoPreset, selectedOptionIds);
        if (!validationResult.isValid()) {
            throw new GenerationException("Invalid settings: " + validationResult.getErrorMessage());
        }

        // 4. Build final settings
        SettingsFile finalSettings = buildFinalSettingsUseCase.execute(francoPreset, selectedOptionIds);

        // 5. Generate seed via API
        try {
            return randomizerApi.generateSeed(request.mode(), finalSettings);
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
