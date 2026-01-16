package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomSaladRupeeUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("shopsanity", '4');
        settings.put("shuffle_pots", "all");
        settings.put("shuffle_crates", "all");
        settings.put("shuffle_freestanding_items", "all");
        settings.put("shuffle_scrubs", "low");
        settings.put("shuffle_beans", true);
        settings.put("shuffle_expensive_merchants", true);
        settings.put("start_with_rupees", true);
        settings.put("correct_potcrate_appearances", "textures_unchecked");
    }
}
