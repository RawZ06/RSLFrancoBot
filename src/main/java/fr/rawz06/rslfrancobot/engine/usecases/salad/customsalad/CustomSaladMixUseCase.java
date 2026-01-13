package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomSaladMixUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("shuffle_interior_entrances", "all");
        settings.put("shuffle_grotto_entrances", true);
        settings.put("shuffle_dungeon_entrances", "all");
        settings.put("shuffle_bosses", "full");
        settings.put("shuffle_ganon_tower", true);
        settings.put("mix_entrance_pools", List.of("Interior", "GrottoGrave", "Dungeon"));
    }
}
