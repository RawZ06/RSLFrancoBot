# 📋 Prochaines Étapes - RSLFrancoBot

## ✅ Ce qui est Fait

- ✅ Architecture Clean complètement refactorée (33 fichiers Java)
- ✅ 3 couches distinctes : Engine, Bot, API
- ✅ Use cases Franco implémentés
- ✅ Use cases RSL/PoT implémentés
- ✅ Handlers Discord découplés de JDA
- ✅ Repository YAML/JSON pour les presets
- ✅ Mocks pour Randomizer API et Python Script
- ✅ Compilation réussie ✅

---

## 🚀 Étape 1 : Tester le Bot (Mode Mock)

### Lancer le bot

```bash
# Vérifier que le token Discord est configuré
cat src/main/resources/application-dev.properties

# Compiler et lancer
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

### Tests à effectuer

- [ ] Le bot se connecte à Discord
- [ ] La commande `/seed` apparaît dans Discord
- [ ] `/seed` affiche 3 boutons (Franco, RSL, PoT)
- [ ] Cliquer "RSL" génère une seed mockée immédiatement
- [ ] Cliquer "PoT" génère une seed mockée immédiatement
- [ ] Cliquer "Franco" affiche le menu d'options
- [ ] Sélectionner des options Franco et valider génère une seed
- [ ] Les logs dans la console sont clairs

### Si ça marche pas

Vérifier :
1. Token Discord dans `application-dev.properties`
2. Bot invité sur un serveur Discord
3. Permissions du bot : `applications.commands`, `bot`
4. Logs dans la console pour voir l'erreur

---

## 🔧 Étape 2 : Implémenter l'API HTTP Randomizer (Remplacement du Mock)

### Créer `OotrHttpClient.java`

```java
package fr.rawz06.rslfrancobot.api.randomizer;

import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRandomizerApi;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Primary // Pour remplacer le Mock
public class OotrHttpClient implements IRandomizerApi {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://ootrandomizer.com/api/v2/seed";

    @Override
    public SeedResult generateSeed(SettingsFile settings) throws RandomizerApiException {
        try {
            // POST vers ootrandomizer.com avec settings.settings()
            // Parser la réponse JSON
            // Retourner SeedResult avec URL et hash
        } catch (Exception e) {
            throw new RandomizerApiException("HTTP call failed", e);
        }
    }
}
```

### Ajouter dépendance HTTP dans `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## 🐍 Étape 3 : Implémenter l'Appel Python (Remplacement du Mock)

### Créer `PythonRSLScriptRunner.java`

```java
package fr.rawz06.rslfrancobot.api.python;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SettingsFile;
import fr.rawz06.rslfrancobot.engine.domain.ports.IRSLScriptRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

@Component
@Primary // Pour remplacer le Mock
public class PythonRSLScriptRunner implements IRSLScriptRunner {

    private final ObjectMapper objectMapper;

    public PythonRSLScriptRunner(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SettingsFile generateSettings(Preset preset) throws ScriptExecutionException {
        try {
            // 1. Écrire le preset dans un fichier temporaire
            File tempInput = Files.createTempFile("preset_", ".json").toFile();
            objectMapper.writeValue(tempInput, preset.baseSettings());

            // 2. Lancer le script Python
            ProcessBuilder pb = new ProcessBuilder(
                "python3",
                "path/to/rsl_script.py",
                tempInput.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new ScriptExecutionException("Script failed with exit code " + exitCode);
            }

            // 3. Lire le fichier de sortie
            File outputFile = new File("path/to/output.json");
            Map<String, Object> settings = objectMapper.readValue(outputFile, Map.class);

            return new SettingsFile(settings);

        } catch (Exception e) {
            throw new ScriptExecutionException("Python script execution failed", e);
        }
    }
}
```

---

## 📦 Étape 4 : Ajouter Presets RSL et PoT Distincts

Actuellement, RSL et PoT utilisent le même preset que Franco.

### Créer `src/main/resources/data/rsl.json`

```json
{
  "create_spoiler": true,
  "user_message": "RSL Mode",
  "world_count": 1,
  ...
}
```

### Créer `src/main/resources/data/pot.json`

```json
{
  "create_spoiler": true,
  "user_message": "PoT Mode",
  "world_count": 1,
  ...
}
```

### Modifier `YamlPresetRepository.java`

```java
private void loadRSLPreset() throws Exception {
    Map<String, Object> baseSettings = loadJsonFile("data/rsl.json");
    Preset rslPreset = new Preset("rsl", baseSettings, List.of());
    presets.put("rsl", rslPreset);
}

private void loadPoTPreset() throws Exception {
    Map<String, Object> baseSettings = loadJsonFile("data/pot.json");
    Preset potPreset = new Preset("pot", baseSettings, List.of());
    presets.put("pot", potPreset);
}
```

---

## 🧪 Étape 5 : Ajouter des Tests Unitaires

### Exemple : Tester `ValidateSettingsUseCase`

```java
package fr.rawz06.rslfrancobot.engine.usecases.franco;

import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidateSettingsUseCaseTest {

    @Test
    void shouldDetectIncompatibility() {
        ValidateSettingsUseCase useCase = new ValidateSettingsUseCase();

        Preset.PresetOption keysy = new Preset.PresetOption(
            "keysy", "Keysy", "Remove small keys",
            Map.of(), List.of("keysanity_all")
        );
        Preset.PresetOption keysanity = new Preset.PresetOption(
            "keysanity_all", "Full Keysanity", "Shuffle all keys",
            Map.of(), List.of("keysy")
        );

        Preset preset = new Preset("test", Map.of(), List.of(keysy, keysanity));

        ValidationResult result = useCase.execute(preset, List.of("keysy", "keysanity_all"));

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Incompatibilité"));
    }

    @Test
    void shouldPassValidation() {
        ValidateSettingsUseCase useCase = new ValidateSettingsUseCase();

        Preset.PresetOption option1 = new Preset.PresetOption(
            "option1", "Option 1", "First option",
            Map.of(), List.of()
        );

        Preset preset = new Preset("test", Map.of(), List.of(option1));

        ValidationResult result = useCase.execute(preset, List.of("option1"));

        assertTrue(result.isValid());
    }
}
```

---

## 🎨 Étape 6 : Améliorations UX Discord

### Envoyer le fichier de settings à l'utilisateur

Dans `SeedPresenter.presentSeedResult()`, ajouter un bouton pour télécharger le JSON :

```java
public DiscordMessage presentSeedResultWithFile(SeedResult result) {
    DiscordMessage message = presentSeedResult(result);
    message.addButton("📥 Télécharger Settings", "download_settings_" + result.seedHash());
    return message;
}
```

Puis dans un nouveau handler :

```java
@Component
public class DownloadSettingsHandler {
    public void handle(DiscordInteraction interaction, SeedResult result) {
        String json = new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(result.usedSettings().settings());

        interaction.sendFile(
            "settings.json",
            json.getBytes(StandardCharsets.UTF_8),
            "application/json"
        );
    }
}
```

---

## 📊 Étape 7 : Monitoring et Logs

### Ajouter Logback configuration

`src/main/resources/logback-spring.xml` :

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="fr.rawz06.rslfrancobot" level="INFO"/>
    <logger name="net.dv8tion.jda" level="WARN"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

---

## 🔐 Étape 8 : Sécurité et Production

### Ne jamais commiter le token Discord

```bash
# Ajouter au .gitignore
echo "src/main/resources/application-dev.properties" >> .gitignore
```

### Utiliser des variables d'environnement

```bash
export DISCORD_TOKEN="your_token_here"
mvn spring-boot:run
```

Dans `application.properties` :

```properties
app.discord.token=${DISCORD_TOKEN:}
```

---

## 📈 Métriques de Succès du Refactoring

| Métrique | Avant | Après |
|----------|-------|-------|
| **Couplage JDA** | Fort | Faible (isolé API Layer) |
| **Testabilité** | Difficile | Facile (use cases purs) |
| **Lignes de code** | ~500 | ~1500 (mais structuré) |
| **Séparation responsabilités** | ❌ | ✅ |
| **Extensibilité** | Limitée | Excellente |
| **Principes SOLID** | Partiels | Tous respectés |

---

## 🎯 Roadmap Long Terme

- [ ] Implémenter HTTP client réel
- [ ] Implémenter Python script runner
- [ ] Ajouter tests unitaires (80%+ coverage)
- [ ] Ajouter presets RSL et PoT distincts
- [ ] Dockeriser l'application
- [ ] CI/CD avec GitHub Actions
- [ ] Base de données pour historique des seeds
- [ ] Dashboard web pour statistiques
- [ ] Support multilingue (FR/EN)

---

**Bon courage pour la suite! L'architecture est solide, maintenant il faut la faire vivre! 🚀**
