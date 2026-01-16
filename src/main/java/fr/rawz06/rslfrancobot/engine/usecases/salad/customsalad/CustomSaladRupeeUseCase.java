package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class CustomSaladRupeeUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("shopsanity", '4');
        String[] keys = {
                "shuffle_pots",
                "shuffle_crates",
                "shuffle_freestanding_items"
        };
        String key = keys[new Random().nextInt(keys.length)];
        settings.put(key, "all");
        settings.put("shuffle_scrubs", "low");
        settings.put("shuffle_beans", true);
        settings.put("shuffle_expensive_merchants", true);
        settings.put("start_with_rupees", true);
        settings.put("correct_potcrate_appearances", "textures_unchecked");
        settings.put("shuffle_silver_rupees", "regional");
        settings.put("silver_rupee_pouches_choice", "all");
    }
}
