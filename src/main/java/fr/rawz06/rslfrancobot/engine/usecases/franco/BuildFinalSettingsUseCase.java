package fr.rawz06.rslfrancobot.engine.usecases.franco;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Case : Construit le fichier de settings final pour le mode Franco.
 * Fusionne le preset de base avec les options sélectionnées par l'utilisateur.
 */
@Component
public class BuildFinalSettingsUseCase {

    public SettingsFile execute(Preset preset, List<String> selectedOptionIds) {
        // Copier les settings de base
        Map<String, Object> finalSettings = new HashMap<>(preset.baseSettings());

        // Si aucune option sélectionnée, retourner les settings de base
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return new SettingsFile(finalSettings);
        }

        // Appliquer les options sélectionnées
        Map<String, Preset.PresetOption> optionsMap = preset.availableOptions().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Preset.PresetOption::id,
                        option -> option
                ));

        for (String selectedId : selectedOptionIds) {
            Preset.PresetOption option = optionsMap.get(selectedId);
            if (option != null && option.settingsToApply() != null) {
                // Fusionner les settings de cette option
                finalSettings.putAll(option.settingsToApply());
            }
        }

        return new SettingsFile(finalSettings);
    }
}
