package fr.rawz06.rslfrancobot.engine.usecases.salad.customsalad;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomSaladSongsUseCase {
    public void custom(Map<String, Object> settings) {
        settings.put("shuffle_song_items", "any");
        settings.put("warp_songs", true);
        settings.put("shuffle_ocarinas", true);
        settings.put("shuffle_cows", true);
        settings.put("shuffle_frog_song_rupees", true);
        settings.put("shuffle_individual_ocarina_notes", true);
        settings.put("starting_inventory", List.of("zeldas_letter"));
    }
}
