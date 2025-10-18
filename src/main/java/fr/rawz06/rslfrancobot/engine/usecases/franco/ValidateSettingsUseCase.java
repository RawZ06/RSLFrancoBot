package fr.rawz06.rslfrancobot.engine.usecases.franco;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Use Case: Validates the consistency of user settings for Franco mode.
 * Checks incompatibilities between selected options.
 */
@Component
public class ValidateSettingsUseCase {

    public ValidationResult execute(Preset preset, List<String> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return ValidationResult.success();
        }

        List<String> errors = new ArrayList<>();

        // Build options map for quick access
        Map<String, Preset.PresetOption> optionsMap = preset.availableOptions().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Preset.PresetOption::id,
                        option -> option
                ));

        // Verify all selected options exist
        for (String selectedId : selectedOptionIds) {
            if (!optionsMap.containsKey(selectedId)) {
                errors.add("Unknown option: " + selectedId);
            }
        }

        if (!errors.isEmpty()) {
            return ValidationResult.failure(errors);
        }

        // Check incompatibilities
        Set<String> selectedSet = Set.copyOf(selectedOptionIds);

        for (String selectedId : selectedOptionIds) {
            Preset.PresetOption option = optionsMap.get(selectedId);
            List<String> incompatibilities = option.incompatibleWith();

            if (incompatibilities != null) {
                for (String incompatibleId : incompatibilities) {
                    if (selectedSet.contains(incompatibleId)) {
                        String incompatibleLabel = optionsMap.get(incompatibleId).label();
                        errors.add(String.format(
                                "Incompatibility detected: '%s' is not compatible with '%s'",
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
