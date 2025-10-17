package fr.rawz06.rslfrancobot.franco;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class SettingParser {

    private final ObjectMapper mapper = new ObjectMapper();

    // 🔒 Fixed list of incompatible preset groups
    private static final List<List<String>> INCOMPATIBILITY = List.of(
            List.of("dungeon_er", "dungeon_er_mixed"),
            List.of("songsanity", "songsanity_dungeon"),
            List.of("token_dj", "token_ow", "token_all"),
            List.of("boss_souls", "regional_souls", "souls"),
            List.of("keysy", "keysanity_all", "keyring_all", "keyring_regional"),
            List.of("minimal", "scarce"),
            List.of(
                    "bridge_6_med",
                    "bridge_4_med_bgk_6_med",
                    "bridge_4_med_bgk_6_dj",
                    "bridge_5_med_bgk_6_med",
                    "bridge_5_med_bgk_6_dj",
                    "bridge_1_stone",
                    "bridge_2_stones",
                    "bridge_3_stones",
                    "bridge_vanilla",
                    "bridge_5_dj",
                    "bridge_6_dj",
                    "bridge_7_dj",
                    "bridge_8_dj",
                    "bridge_9_dj",
                    "precompleted_1",
                    "precompleted_2",
                    "precompleted_3"
            )
    );

    /**
     * Loads the base JSON and applies the YAML presets specified.
     * Validates incompatibilities before applying.
     *
     * @param settingsKeys List of preset keys to apply.
     * @return Final JSON string ready for a POST request body.
     */
    public String parse(List<String> settingsKeys) throws IOException {
        // 1️⃣ Validate incompatibilities
        checkIncompatibilities(settingsKeys);

        // 2️⃣ Load the base JSON
        Map<String, Object> base = loadBaseJson();

        // 3️⃣ Load the YAML presets
        Map<String, Object> presets = loadYamlPresets();

        // 4️⃣ Apply each selected preset
        for (String key : settingsKeys) {
            Map<String, Object> preset = (Map<String, Object>) presets.get(key);
            if (preset == null) {
                System.err.println("⚠️ Preset not found in franco.yaml: " + key);
                continue;
            }

            Map<String, Object> presetSettings = (Map<String, Object>) preset.get("settings");
            if (presetSettings == null) continue;

            applySettings(base, presetSettings);
        }

        // 5️⃣ Return the final JSON string
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(base);
        } catch (JsonProcessingException e) {
            throw new IOException("JSON serialization error", e);
        }
    }

    /** Checks that no incompatible presets were selected. */
    private void checkIncompatibilities(List<String> settingsKeys) {
        for (List<String> group : INCOMPATIBILITY) {
            List<String> selectedInGroup = settingsKeys.stream()
                    .filter(group::contains)
                    .toList();

            if (selectedInGroup.size() > 1) {
                throw new IllegalArgumentException(
                        "Incompatibility detected between presets: "
                                + String.join(", ", selectedInGroup)
                );
            }
        }
    }

    /** Applies the YAML preset keys/values into the base JSON map. */
    private void applySettings(Map<String, Object> base, Map<String, Object> settings) {
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            base.put(entry.getKey(), entry.getValue());
        }
    }

    /** Loads the base JSON file from resources. */
    private Map<String, Object> loadBaseJson() throws IOException {
        try (InputStream in = SettingParser.class.getResourceAsStream("/data/franco.json")) {
            if (in == null)
                throw new IOException("❌ File not found: /resources/data/franco.json");
            return mapper.readValue(in, Map.class);
        }
    }

    /** Loads the YAML presets from resources. */
    private Map<String, Object> loadYamlPresets() throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = SettingParser.class.getResourceAsStream("/data/franco.yaml")) {
            if (in == null)
                throw new IOException("❌ File not found: /resources/data/franco.yaml");
            return yaml.load(in);
        }
    }
}