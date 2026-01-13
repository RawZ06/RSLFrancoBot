package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomSaladDungeonUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("shuffle_dungeon_entrances", "simple");
        settings.put("shuffle_bosses", "full");
        settings.put("tokensanity", "dungeons");
        settings.put("keyring_give_bk", true);
        settings.put("key_rings_choice", "all");
        settings.put("shuffle_smallkeys", "regional");
        settings.put("shuffle_bosskeys", "regional");
    }
}
