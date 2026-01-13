package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomSaladBossUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("shuffle_dungeon_entrances", "simple");
        settings.put("shuffle_bosses", "full");
        settings.put("shuffle_bosskeys", "regional");
    }
}
