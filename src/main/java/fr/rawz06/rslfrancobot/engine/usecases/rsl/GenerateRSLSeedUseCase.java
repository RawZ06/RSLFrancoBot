package fr.rawz06.rslfrancobot.engine.usecases.rsl;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRSLScriptRunner;
import org.springframework.stereotype.Component;

/**
 * Use Case : Génère une seed en mode RSL (ou PoT).
 * Utilise le script Python pour générer les settings aléatoires, puis génère la seed.
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
        // Déterminer le nom du preset en fonction du mode
        String presetName = switch (request.mode()) {
            case RSL -> "rsl";
            case POT -> "pot";
            default -> throw new GenerationException("Mode non supporté pour RSL/PoT : " + request.mode());
        };

        // 1. Récupérer le preset
        Preset preset = presetRepository.getPreset(presetName)
                .orElseThrow(() -> new GenerationException("Preset " + presetName + " introuvable"));

        // 2. Générer les settings via le script Python
        SettingsFile generatedSettings;
        try {
            generatedSettings = rslScriptRunner.generateSettings(preset);
        } catch (IRSLScriptRunner.ScriptExecutionException e) {
            throw new GenerationException("Erreur lors de l'exécution du script RSL", e);
        }

        // 3. Générer la seed via l'API
        try {
            return randomizerApi.generateSeed(generatedSettings);
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
