package fr.rawz06.rslfrancobot.api.python;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRSLScriptRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation Mock du script Python RSL.
 * Simule l'exécution du script sans réellement l'appeler.
 * À remplacer par une vraie implémentation ProcessBuilder plus tard.
 */
@Component
public class MockRSLScriptAdapter implements IRSLScriptRunner {

    @Override
    public SettingsFile generateSettings(Preset preset) throws ScriptExecutionException {
        // Simuler un délai d'exécution du script
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Interruption pendant l'exécution du script", e);
        }

        // Pour le mock, on retourne simplement les settings de base du preset
        // avec quelques modifications aléatoires simulées
        Map<String, Object> mockSettings = new HashMap<>(preset.baseSettings());

        // Simuler quelques changements aléatoires
        mockSettings.put("seed", String.valueOf(System.currentTimeMillis()));
        mockSettings.put("generated_by", "MockRSLScript");

        return new SettingsFile(mockSettings);
    }
}
