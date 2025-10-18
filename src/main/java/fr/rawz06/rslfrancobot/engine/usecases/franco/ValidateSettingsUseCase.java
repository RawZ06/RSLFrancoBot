package fr.rawz06.rslfrancobot.engine.usecases.franco;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Use Case : Valide la cohérence des settings utilisateur pour le mode Franco.
 * Vérifie les incompatibilités entre les options sélectionnées.
 */
@Component
public class ValidateSettingsUseCase {

    public ValidationResult execute(Preset preset, List<String> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return ValidationResult.success();
        }

        List<String> errors = new ArrayList<>();

        // Construire une map des options pour accès rapide
        Map<String, Preset.PresetOption> optionsMap = preset.availableOptions().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Preset.PresetOption::id,
                        option -> option
                ));

        // Vérifier que toutes les options sélectionnées existent
        for (String selectedId : selectedOptionIds) {
            if (!optionsMap.containsKey(selectedId)) {
                errors.add("Option inconnue : " + selectedId);
            }
        }

        if (!errors.isEmpty()) {
            return ValidationResult.failure(errors);
        }

        // Vérifier les incompatibilités
        Set<String> selectedSet = Set.copyOf(selectedOptionIds);

        for (String selectedId : selectedOptionIds) {
            Preset.PresetOption option = optionsMap.get(selectedId);
            List<String> incompatibilities = option.incompatibleWith();

            if (incompatibilities != null) {
                for (String incompatibleId : incompatibilities) {
                    if (selectedSet.contains(incompatibleId)) {
                        String incompatibleLabel = optionsMap.get(incompatibleId).label();
                        errors.add(String.format(
                                "Incompatibilité détectée : '%s' n'est pas compatible avec '%s'",
                                option.label(),
                                incompatibleLabel
                        ));
                    }
                }
            }
        }

        return errors.isEmpty()
                ? ValidationResult.success()
                : ValidationResult.failure(errors);
    }
}
