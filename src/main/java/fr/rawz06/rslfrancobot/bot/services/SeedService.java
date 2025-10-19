package fr.rawz06.rslfrancobot.bot.services;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedRequest;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.usecases.franco.GenerateFrancoSeedUseCase;
import fr.rawz06.rslfrancobot.engine.usecases.rsl.GenerateRSLSeedUseCase;
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
    private final IPresetRepository presetRepository;

    public SeedService(
            GenerateFrancoSeedUseCase generateFrancoSeedUseCase,
            GenerateRSLSeedUseCase generateRSLSeedUseCase,
            IPresetRepository presetRepository
    ) {
        this.generateFrancoSeedUseCase = generateFrancoSeedUseCase;
        this.generateRSLSeedUseCase = generateRSLSeedUseCase;
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
            };
        } catch (GenerateFrancoSeedUseCase.GenerationException | GenerateRSLSeedUseCase.GenerationException e) {
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
