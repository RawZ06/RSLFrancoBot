package fr.rawz06.rslfrancobot.engine.usecases.franco;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Case: Builds the final settings file for Franco mode.
 * Merges the base preset with user-selected options.
 */
@Component
public class BuildFinalSettingsUseCase {

    public SettingsFile execute(Preset preset, List<String> selectedOptionIds) {
        // Copy base settings
        Map<String, Object> finalSettings = new HashMap<>(preset.baseSettings());

        // If no options selected, return base settings
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return new SettingsFile(finalSettings);
        }

        // Apply selected options
        Map<String, Preset.PresetOption> optionsMap = preset.availableOptions().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Preset.PresetOption::id,
                        option -> option
                ));

        for (String selectedId : selectedOptionIds) {
            Preset.PresetOption option = optionsMap.get(selectedId);
            if (option != null && option.settingsToApply() != null) {
                // Merge settings from this option
                finalSettings.putAll(option.settingsToApply());
            }
        }

        return new SettingsFile(finalSettings);
    }
}
