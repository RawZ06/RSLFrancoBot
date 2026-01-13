package fr.rawz06.rslfrancobot.engine.domain.entities;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of user settings validation.
 * Indicates whether the configuration is valid and contains any errors.
 */
@Getter
public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = Collections.unmodifiableList(errors);
    }

    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult failure(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            throw new IllegalArgumentException("Errors cannot be empty for a failure result");
        }
        return new ValidationResult(false, new ArrayList<>(errors));
    }

    public static ValidationResult failure(String error) {
        return failure(List.of(error));
    }

    public String getErrorMessage() {
        return String.join("\n", errors);
    }
}
