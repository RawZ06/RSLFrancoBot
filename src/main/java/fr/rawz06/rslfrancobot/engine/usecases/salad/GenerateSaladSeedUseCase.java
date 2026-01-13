package fr.rawz06.rslfrancobot.engine.usecases.salad;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedRequest;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import fr.rawz06.rslfrancobot.engine.usecases.allsanity.GenerateAllsanitySeedUseCase;
import fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Use Case: Generates a seed in Salad mode.
 * Uses fixed settings from salad.json - no user configuration.
 */
@Component
@RequiredArgsConstructor
public class GenerateSaladSeedUseCase {

    private final IPresetRepository presetRepository;
    private final IRandomizerApi randomizerApi;

    private final CustomSaladBossUseCase customSaladBossUseCase;
    private final CustomSaladRupeeUseCase customSaladRupeeUseCase;
    private final CustomSaladDungeonUseCase customSaladDungeonUseCase;
    private final CustomSaladSongsUseCase customSaladSongsUseCase;
    private final CustomSaladMixUseCase customSaladMixUseCase;

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Retrieve salad preset (contains fixed settings from salad.json)
        Preset preset = presetRepository.getPreset("salad")
                .orElseThrow(() -> new GenerationException("Salad preset not found"));

        // 2. Create a mutable copy of base settings
        Map<String, Object> settings = new HashMap<>(preset.baseSettings());

        // 3. Apply mode-specific modifications
        switch (request.mode()) {
            case SALAD_BOSS:
                customSaladBossUseCase.custom(settings);
                break;
            case SALAD_RUPEES:
                customSaladRupeeUseCase.custom(settings);
                break;
            case SALAD_DUNGEONS:
                customSaladDungeonUseCase.custom(settings);
                break;
            case SALAD_SONGS:
                customSaladSongsUseCase.custom(settings);
                break;
            case SALAD_MIX:
                customSaladMixUseCase.custom(settings);
                break;
            case SALAD_ALL:
                customSaladBossUseCase.custom(settings);
                customSaladRupeeUseCase.custom(settings);
                customSaladDungeonUseCase.custom(settings);
                customSaladSongsUseCase.custom(settings);
                customSaladMixUseCase.custom(settings);
                settings.put("shuffle_pots", "off");
                settings.put("shuffle_crates", "off");
                settings.put("shuffle_freestanding_items", "off");
                break;
            default:
                throw new GenerateSaladSeedUseCase.GenerationException("Unsupported Salad mode: " + request.mode());
        }

        // 4. Create SettingsFile from modified settings
        SettingsFile settingsFile = new SettingsFile(settings);

        // 5. Generate seed via API
        try {
            return randomizerApi.generateSeed(request.mode(), settingsFile);
        } catch (IRandomizerApi.RandomizerApiException e) {
            throw new GenerateSaladSeedUseCase.GenerationException("Error during seed generation", e);
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
