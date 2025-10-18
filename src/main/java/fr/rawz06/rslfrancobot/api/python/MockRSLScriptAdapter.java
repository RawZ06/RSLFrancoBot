package fr.rawz06.rslfrancobot.api.python;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRSLScriptRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of the RSL Python script.
 * Simulates script execution without actually calling it.
 * To be replaced with real ProcessBuilder implementation later.
 */
@Component
public class MockRSLScriptAdapter implements IRSLScriptRunner {

    @Override
    public SettingsFile generateSettings(Preset preset) throws ScriptExecutionException {
        // Simulate script execution delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Script execution interrupted", e);
        }

        // For the mock, simply return preset base settings
        // with some simulated random modifications
        Map<String, Object> mockSettings = new HashMap<>(preset.baseSettings());

        // Simulate some random changes
        mockSettings.put("seed", String.valueOf(System.currentTimeMillis()));
        mockSettings.put("generated_by", "MockRSLScript");

        return new SettingsFile(mockSettings);
    }
}
