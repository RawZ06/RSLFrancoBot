package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomSaladEnemyUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("enemizer", "on");
        settings.put("shuffle_enemy_spawns", "regional");
    }
}
