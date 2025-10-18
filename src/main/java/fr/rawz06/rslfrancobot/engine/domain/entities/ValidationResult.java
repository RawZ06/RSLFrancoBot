package fr.rawz06.rslfrancobot.engine.domain.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Résultat de la validation des settings utilisateur.
 * Indique si la configuration est valide et contient les éventuelles erreurs.
 */
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
            throw new IllegalArgumentException("Les erreurs ne peuvent pas être vides pour un résultat échec");
        }
        return new ValidationResult(false, new ArrayList<>(errors));
    }

    public static ValidationResult failure(String error) {
        return failure(List.of(error));
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getErrorMessage() {
        return String.join("\n", errors);
    }
}
