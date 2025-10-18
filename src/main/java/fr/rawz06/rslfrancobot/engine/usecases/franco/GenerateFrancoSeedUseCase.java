package fr.rawz06.rslfrancobot.engine.usecases.franco;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use Case : Génère une seed en mode Franco.
 * Orchestre la validation, la construction des settings et la génération de la seed.
 */
@Component
public class GenerateFrancoSeedUseCase {

    private final IPresetRepository presetRepository;
    private final IRandomizerApi randomizerApi;
    private final ValidateSettingsUseCase validateSettingsUseCase;
    private final BuildFinalSettingsUseCase buildFinalSettingsUseCase;

    public GenerateFrancoSeedUseCase(
            IPresetRepository presetRepository,
            IRandomizerApi randomizerApi,
            ValidateSettingsUseCase validateSettingsUseCase,
            BuildFinalSettingsUseCase buildFinalSettingsUseCase
    ) {
        this.presetRepository = presetRepository;
        this.randomizerApi = randomizerApi;
        this.validateSettingsUseCase = validateSettingsUseCase;
        this.buildFinalSettingsUseCase = buildFinalSettingsUseCase;
    }

    public SeedResult execute(SeedRequest request) throws GenerationException {
        // 1. Récupérer le preset Franco
        Preset francoPreset = presetRepository.getPreset("franco")
                .orElseThrow(() -> new GenerationException("Preset Franco introuvable"));

        // 2. Extraire les IDs des options sélectionnées
        List<String> selectedOptionIds = request.userSettings() != null
                ? List.copyOf(request.userSettings().keySet())
                : List.of();

        // 3. Valider les settings
        ValidationResult validationResult = validateSettingsUseCase.execute(francoPreset, selectedOptionIds);
        if (!validationResult.isValid()) {
            throw new GenerationException("Settings invalides : " + validationResult.getErrorMessage());
        }

        // 4. Construire les settings finaux
        SettingsFile finalSettings = buildFinalSettingsUseCase.execute(francoPreset, selectedOptionIds);

        // 5. Générer la seed via l'API
        try {
            return randomizerApi.generateSeed(finalSettings);
        } catch (IRandomizerApi.RandomizerApiException e) {
            throw new GenerationException("Erreur lors de la génération de la seed", e);
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
