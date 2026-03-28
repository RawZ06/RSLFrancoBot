package fr.rawz06.rslfrancobot.engine.usecases.rsl;

import fr.rawz06.rslfrancobot.engine.domain.entities.*;
import fr.rawz06.rslfrancobot.engine.domain.ports.RSLSettingsFetcher;
import fr.rawz06.rslfrancobot.engine.domain.ports.RandomizerApi;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Use Case: Generates a seed in RSL mode by fetching settings from an API.
 */
@Component
public class GenerateRSLSeedFromAPIUseCase {

    private final RSLSettingsFetcher settingsFetcher;
    private final RandomizerApi randomizerApi;

    public GenerateRSLSeedFromAPIUseCase(
            RSLSettingsFetcher settingsFetcher,
            RandomizerApi randomizerApi
    ) {
        this.settingsFetcher = settingsFetcher;
        this.randomizerApi = randomizerApi;
    }

    public SeedResult execute(SeedRequest request, String season) throws GenerationException {
        // 1. Fetch settings from API
        SettingsFile generatedSettings;
        try {
            generatedSettings = settingsFetcher.fetchSettings(season);
        } catch (RSLSettingsFetcher.SettingsFetcherException e) {
            throw new GenerationException("Error fetching RSL settings from API", e);
        }

        // 2. Extract and format settings
        // API response already has "settings" flattened in the implementation
        Map<String, Object> settingsMap = new HashMap<>(generatedSettings.settings());

        // Add hardcoded settings (consistent with GenerateRSLSeedUseCase)
        settingsMap.put("show_seed_info", true);
        settingsMap.put("create_spoiler", true);
        settingsMap.put("password_lock", false);

        SettingsFile finalSettings = new SettingsFile(settingsMap);

        // 3. Generate seed via API
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
