package fr.rawz06.rslfrancobot.franco;

import fr.rawz06.rslfrancobot.discord.models.ChooseMenu;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FrancoOptionBuilder {

    public ChooseMenu build() throws IOException {
        Map<String, Object> map = load();
        ChooseMenu.ChooseMenuBuilder builder = ChooseMenu.builder();
        builder.name("franco_selector")
                .placeholder("Franco Settings Selector")
                .minValue(0)
                .maxValue(25)
                .message("Choose settings for your franco tournament seed");

        List<ChooseMenu.ChooseMenuItem> items = new ArrayList<>();

        for(Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> node = (Map<String, Object>) entry.getValue();

            String label = (String) node.getOrDefault("label", key);
            String description = (String) node.getOrDefault("description", "No description provided.");

            ChooseMenu.ChooseMenuItem item = ChooseMenu.ChooseMenuItem.builder()
                    .label(label)
                    .description(description)
                    .value(key)
                    .build();

            items.add(item);
        }
        builder.options(items);
        return builder.build();
    }

    private Map<String, Object> load() throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = FrancoOptionBuilder.class.getResourceAsStream("/data/franco.yaml")) {
            return yaml.load(in);
        }
    }
}
