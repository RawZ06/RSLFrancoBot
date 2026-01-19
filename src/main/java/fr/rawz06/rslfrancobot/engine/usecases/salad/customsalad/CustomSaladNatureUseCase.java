package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomSaladNatureUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("shuffle_cows", true);
        settings.put("shuffle_boulders", true);
        settings.put("golden_boulders", true);
        settings.put("tokensanity", "all");
        settings.put("shuffle_grass", true);
        settings.put("shuffle_beehives", true);
        settings.put("correct_potcrate_appearances", "textures_unchecked");
    }
}