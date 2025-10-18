package fr.rawz06.rslfrancobot.engine.domain.ports;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;

/**
 * Port pour l'exécution du script Python RSL.
 * Abstraction de l'appel au script de génération aléatoire.
 * Implémenté dans l'API Layer.
 */
public interface IRSLScriptRunner {
    /**
     * Exécute le script Python pour générer des settings RSL.
     *
     * @param preset Le preset de base (RSL ou PoT)
     * @return Les settings générés par le script
     * @throws ScriptExecutionException Si l'exécution du script échoue
     */
    SettingsFile generateSettings(Preset preset) throws ScriptExecutionException;

    class ScriptExecutionException extends Exception {
        public ScriptExecutionException(String message) {
            super(message);
        }

        public ScriptExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
