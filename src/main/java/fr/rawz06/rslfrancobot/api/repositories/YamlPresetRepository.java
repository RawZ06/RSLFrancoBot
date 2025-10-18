package fr.rawz06.rslfrancobot.api.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.ports.IPresetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * Implémentation du repository de presets.
 * Charge les presets depuis les fichiers YAML et JSON dans resources/data.
 */
@Repository
public class YamlPresetRepository implements IPresetRepository {

    private static final Logger logger = LoggerFactory.getLogger(YamlPresetRepository.class);
    private final Map<String, Preset> presets = new HashMap<>();
    private final ObjectMapper objectMapper;

    public YamlPresetRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        loadPresets();
    }

    private void loadPresets() {
        try {
            loadFrancoPreset();
            loadRSLPreset();
            loadPoTPreset();
            logger.info("Presets chargés avec succès : {}", presets.keySet());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des presets", e);
        }
    }

    private void loadFrancoPreset() throws Exception {
        // Charger le fichier JSON de base
        Map<String, Object> baseSettings = loadJsonFile("data/franco.json");

        // Charger les options depuis le YAML
        List<Preset.PresetOption> options = loadFrancoOptionsFromYaml();

        Preset francoPreset = new Preset("franco", baseSettings, options);
        presets.put("franco", francoPreset);
    }

    private void loadRSLPreset() {
        // Pour RSL, on utilise aussi franco.json comme base
        // mais sans options configurables (généré par Python)
        try {
            Map<String, Object> baseSettings = loadJsonFile("data/franco.json");
            Preset rslPreset = new Preset("rsl", baseSettings, List.of());
            presets.put("rsl", rslPreset);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du preset RSL", e);
        }
    }

    private void loadPoTPreset() {
        // Pour PoT, similaire à RSL
        try {
            Map<String, Object> baseSettings = loadJsonFile("data/franco.json");
            Preset potPreset = new Preset("pot", baseSettings, List.of());
            presets.put("pot", potPreset);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du preset PoT", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Preset.PresetOption> loadFrancoOptionsFromYaml() throws Exception {
        Yaml yaml = new Yaml();
        List<Preset.PresetOption> options = new ArrayList<>();

        try (InputStream inputStream = new ClassPathResource("data/franco.yaml").getInputStream()) {
            Map<String, Map<String, Object>> yamlData = yaml.load(inputStream);

            for (Map.Entry<String, Map<String, Object>> entry : yamlData.entrySet()) {
                String optionId = entry.getKey();
                Map<String, Object> optionData = entry.getValue();

                String label = (String) optionData.get("label");
                String description = (String) optionData.get("description");
                Map<String, Object> settingsToApply = (Map<String, Object>) optionData.get("settings");

                // Gérer les incompatibilités (à enrichir selon tes besoins)
                List<String> incompatibleWith = detectIncompatibilities(optionId);

                Preset.PresetOption option = new Preset.PresetOption(
                        optionId,
                        label,
                        description,
                        settingsToApply,
                        incompatibleWith
                );

                options.add(option);
            }
        }

        return options;
    }

    /**
     * Détecte les incompatibilités entre options.
     * Basé sur la logique de l'ancien SettingParser.
     */
    private List<String> detectIncompatibilities(String optionId) {
        // Liste des incompatibilités connues
        Map<String, List<String>> incompatibilitiesMap = Map.ofEntries(
                Map.entry("keysy", List.of("keysanity_all", "keyring_all", "keyring_regional")),
                Map.entry("keysanity_all", List.of("keysy", "keyring_all", "keyring_regional")),
                Map.entry("keyring_all", List.of("keysy", "keysanity_all", "keyring_regional")),
                Map.entry("keyring_regional", List.of("keysy", "keysanity_all", "keyring_all")),
                Map.entry("token_dj", List.of("token_ow", "token_all")),
                Map.entry("token_ow", List.of("token_dj", "token_all")),
                Map.entry("token_all", List.of("token_dj", "token_ow")),
                Map.entry("songsanity", List.of("songsanity_dungeon")),
                Map.entry("songsanity_dungeon", List.of("songsanity")),
                Map.entry("dungeon_er", List.of("dungeon_er_mixed")),
                Map.entry("dungeon_er_mixed", List.of("dungeon_er")),
                Map.entry("boss_souls", List.of("regional_souls", "souls")),
                Map.entry("regional_souls", List.of("boss_souls", "souls")),
                Map.entry("souls", List.of("boss_souls", "regional_souls")),
                Map.entry("minimal", List.of("scarce")),
                Map.entry("scarce", List.of("minimal")),
                // Tous les bridge sont incompatibles entre eux
                Map.entry("bridge_6_med", List.of("bridge_4_med_bgk_6_med", "bridge_4_med_bgk_6_dj", "bridge_5_med_bgk_6_med", "bridge_5_med_bgk_6_dj", "bridge_1_stone", "bridge_2_stones", "bridge_3_stones", "bridge_vanilla", "bridge_5_dj", "bridge_6_dj", "bridge_7_dj", "bridge_8_dj", "bridge_9_dj")),
                Map.entry("bridge_4_med_bgk_6_med", List.of("bridge_6_med", "bridge_4_med_bgk_6_dj", "bridge_5_med_bgk_6_med", "bridge_5_med_bgk_6_dj", "bridge_1_stone", "bridge_2_stones", "bridge_3_stones", "bridge_vanilla", "bridge_5_dj", "bridge_6_dj", "bridge_7_dj", "bridge_8_dj", "bridge_9_dj")),
                Map.entry("bridge_4_med_bgk_6_dj", List.of("bridge_6_med", "bridge_4_med_bgk_6_med", "bridge_5_med_bgk_6_med", "bridge_5_med_bgk_6_dj", "bridge_1_stone", "bridge_2_stones", "bridge_3_stones", "bridge_vanilla", "bridge_5_dj", "bridge_6_dj", "bridge_7_dj", "bridge_8_dj", "bridge_9_dj")),
                Map.entry("precompleted_1", List.of("precompleted_2", "precompleted_3")),
                Map.entry("precompleted_2", List.of("precompleted_1", "precompleted_3")),
                Map.entry("precompleted_3", List.of("precompleted_1", "precompleted_2"))
        );

        return incompatibilitiesMap.getOrDefault(optionId, List.of());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadJsonFile(String path) throws Exception {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readValue(inputStream, Map.class);
        }
    }

    @Override
    public Optional<Preset> getPreset(String name) {
        return Optional.ofNullable(presets.get(name.toLowerCase()));
    }

    @Override
    public boolean presetExists(String name) {
        return presets.containsKey(name.toLowerCase());
    }
}
