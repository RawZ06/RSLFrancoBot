package fr.rawz06.rslfrancobot.bot.services;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedRequest;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.usecases.allsanity.GenerateAllsanitySeedUseCase;
import fr.rawz06.rslfrancobot.engine.usecases.franco.GenerateFrancoSeedUseCase;
import fr.rawz06.rslfrancobot.engine.usecases.rsl.GenerateRSLSeedUseCase;
import fr.rawz06.rslfrancobot.engine.usecases.s8.GenerateS8SeedUseCase;
import fr.rawz06.rslfrancobot.engine.usecases.s9.GenerateS9SeedUseCase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Application service that coordinates domain use cases.
 * Entry point from Bot Layer to Engine Layer.
 */
@Service
public class SeedService {

    private final GenerateFrancoSeedUseCase generateFrancoSeedUseCase;
    private final GenerateRSLSeedUseCase generateRSLSeedUseCase;
    private final GenerateS8SeedUseCase generateS8SeedUseCase;
    private final GenerateS9SeedUseCase generateS9SeedUseCase;
    private final GenerateAllsanitySeedUseCase generateAllsanitySeedUseCase;
    private final IPresetRepository presetRepository;

    public SeedService(
            GenerateFrancoSeedUseCase generateFrancoSeedUseCase,
            GenerateRSLSeedUseCase generateRSLSeedUseCase,
            GenerateS8SeedUseCase generateS8SeedUseCase,
            GenerateS9SeedUseCase generateS9SeedUseCase,
            GenerateAllsanitySeedUseCase generateAllsanitySeedUseCase,
            IPresetRepository presetRepository
    ) {
        this.generateFrancoSeedUseCase = generateFrancoSeedUseCase;
        this.generateRSLSeedUseCase = generateRSLSeedUseCase;
        this.generateS8SeedUseCase = generateS8SeedUseCase;
        this.generateS9SeedUseCase = generateS9SeedUseCase;
        this.generateAllsanitySeedUseCase = generateAllsanitySeedUseCase;
        this.presetRepository = presetRepository;
    }

    /**
     * Generates a seed according to the requested mode.
     */
    public SeedResult generateSeed(SeedMode mode, String userId, Map<String, String> userSettings) throws SeedGenerationException {
        SeedRequest request = new SeedRequest(mode, userId, userSettings);

        try {
            return switch (mode) {
                case FRANCO -> generateFrancoSeedUseCase.execute(request);
                case RSL, POT, BEGINNER -> generateRSLSeedUseCase.execute(request);
                case S8 -> generateS8SeedUseCase.execute(request);
                case S9 -> generateS9SeedUseCase.execute(request);
                case ALLSANITY_ER_DECOUPLED, ALLSANITY_ER, ALLSANITY_ONLY -> generateAllsanitySeedUseCase.execute(request);
            };
        } catch (GenerateFrancoSeedUseCase.GenerationException | GenerateRSLSeedUseCase.GenerationException | GenerateS8SeedUseCase.GenerationException | GenerateS9SeedUseCase.GenerationException | GenerateAllsanitySeedUseCase.GenerationException e) {
            throw new SeedGenerationException("Error during seed generation", e);
        }
    }

    /**
     * Retrieves available options for a given preset.
     */
    public List<Preset.PresetOption> getAvailableOptions(String presetName) {
        return presetRepository.getPreset(presetName)
                .map(Preset::availableOptions)
                .orElseThrow(() -> new IllegalArgumentException("Preset not found: " + presetName));
    }

    public static class SeedGenerationException extends Exception {
        public SeedGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
