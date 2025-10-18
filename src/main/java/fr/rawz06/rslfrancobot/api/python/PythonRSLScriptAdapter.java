package fr.rawz06.rslfrancobot.api.python;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRSLScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implémentation réelle du script Python RSL.
 * Exécute le script RandomSettingsGenerator.py pour générer des settings aléatoires.
 */
@Component
@Primary // Remplace le Mock
public class PythonRSLScriptAdapter implements IRSLScriptRunner {

    private static final Logger logger = LoggerFactory.getLogger(PythonRSLScriptAdapter.class);
    private static final Pattern FILENAME_PATTERN = Pattern.compile("Plando File: (.+\\.json)");

    @Value("${app.python.command}")
    private String pythonCommand;

    @Value("${app.python.script.dir}")
    private String scriptDir;

    @Value("${app.python.script.name}")
    private String scriptName;

    @Value("${app.python.weights.rsl}")
    private String rslWeight;

    @Value("${app.python.weights.pot}")
    private String potWeight;

    private final ObjectMapper objectMapper;

    public PythonRSLScriptAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SettingsFile generateSettings(Preset preset) throws ScriptExecutionException {
        String weightFile = preset.name().equals("rsl") ? rslWeight : potWeight;

        logger.info("Génération de settings {} avec le script Python...", preset.name());
        logger.info("Weight file: {}", weightFile);

        try {
            // 1. Préparer la commande
            ProcessBuilder pb = new ProcessBuilder(
                    pythonCommand,
                    scriptName,
                    "--override", weightFile,
                    "--no_seed"
            );

            // Définir le répertoire de travail
            File workingDir = new File(scriptDir);
            if (!workingDir.exists() || !workingDir.isDirectory()) {
                throw new ScriptExecutionException("Script directory not found: " + scriptDir);
            }
            pb.directory(workingDir);
            pb.redirectErrorStream(true);

            logger.info("Commande: {} dans {}", String.join(" ", pb.command()), workingDir.getAbsolutePath());

            // 2. Exécuter le script
            Process process = pb.start();

            // 3. Capturer la sortie
            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            int exitCode = process.waitFor();
            logger.info("Script terminé avec le code: {}", exitCode);
            logger.debug("Output du script:\n{}", output);

            if (exitCode != 0) {
                throw new ScriptExecutionException("Script failed with exit code " + exitCode + ": " + output);
            }

            // 4. Extraire le nom du fichier généré
            String filename = extractFilename(output);
            logger.info("Fichier généré: {}", filename);

            // 5. Lire le fichier JSON
            File generatedFile = new File(workingDir, "data/" + filename);
            if (!generatedFile.exists()) {
                throw new ScriptExecutionException("Generated file not found: " + generatedFile.getAbsolutePath());
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> settings = objectMapper.readValue(generatedFile, Map.class);
            logger.info("Settings lus avec succès ({} clés)", settings.size());

            // 6. Supprimer le fichier temporaire
            boolean deleted = generatedFile.delete();
            if (!deleted) {
                logger.warn("Impossible de supprimer le fichier temporaire: {}", generatedFile.getAbsolutePath());
            } else {
                logger.debug("Fichier temporaire supprimé: {}", filename);
            }

            return new SettingsFile(settings);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Script execution interrupted", e);
        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution du script Python", e);
            throw new ScriptExecutionException("Python script execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Extrait le nom du fichier depuis la sortie du script.
     * Cherche la ligne "Plando File: XXX.json"
     */
    private String extractFilename(String output) throws ScriptExecutionException {
        Matcher matcher = FILENAME_PATTERN.matcher(output);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new ScriptExecutionException(
                "Could not extract filename from script output. Looking for 'Plando File: XXX.json' pattern.\n" +
                "Output was:\n" + output
        );
    }
}
