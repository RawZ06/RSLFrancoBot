package fr.rawz06.rslfrancobot.web;

import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
public class SeedRestController {

    private final SeedService seedService;

    private static final Map<String, SeedMode> MODE_MAPPING;

    static {
        Map<String, SeedMode> map = new java.util.HashMap<>();
        // Classic modes (except Franco)
        map.put("seed_s8", SeedMode.S8);
        map.put("seed_s9", SeedMode.S9);
        map.put("seed_tot", SeedMode.TOT);
        map.put("seed_mixed", SeedMode.MIXED);

        // RSL modes
        map.put("seed_rsl", SeedMode.RSL);
        map.put("seed_pot", SeedMode.POT);
        map.put("seed_beginner", SeedMode.BEGINNER);
        map.put("seed_rsl_s8", SeedMode.RSL_SEASON8);
        map.put("seed_rot", SeedMode.ROT);

        // Allsanity modes
        map.put("seed_allsanity_er_decoupled", SeedMode.ALLSANITY_ER_DECOUPLED);
        map.put("seed_allsanity_er", SeedMode.ALLSANITY_ER);
        map.put("seed_allsanity_only", SeedMode.ALLSANITY_ONLY);

        // Salad modes
        map.put("seed_salad_enemy", SeedMode.SALAD_ENEMY);
        map.put("seed_salad_rupee", SeedMode.SALAD_RUPEES);
        map.put("seed_salad_songs", SeedMode.SALAD_SONGS);
        map.put("seed_salad_dungeon", SeedMode.SALAD_DUNGEONS);
        map.put("seed_salad_mix", SeedMode.SALAD_MIX);
        map.put("seed_salad_all", SeedMode.SALAD_ALL);
        map.put("seed_salad_nature", SeedMode.SALAD_NATURE);

        MODE_MAPPING = Collections.unmodifiableMap(map);
    }

    @GetMapping("/{modeId}")
    public SeedResult generateSeed(@PathVariable String modeId, @RequestParam(required = false, defaultValue = "api-user") String userId) {
        SeedMode mode = MODE_MAPPING.get(modeId);
        if (mode == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mode not found or not supported via API: " + modeId);
        }

        try {
            return seedService.generateSeed(mode, userId, Map.of());
        } catch (SeedService.SeedGenerationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during seed generation: " + e.getMessage(), e);
        }
    }
}
