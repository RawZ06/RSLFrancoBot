package fr.rawz06.rslfrancobot.web;

import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class SeedRestController {

    private final SeedService seedService;

    @GetMapping("/{modeId}")
    public SeedResult generateSeed(@PathVariable String modeId, @RequestParam(required = false, defaultValue = "api-user") String userId) {
        SeedMode mode = SeedModeAPI.fromApiId(modeId)
                .map(SeedModeAPI::getSeedMode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mode not found or not supported via API: " + modeId));

        try {
            return seedService.generateSeed(mode, userId, Map.of());
        } catch (SeedService.SeedGenerationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during seed generation: " + e.getMessage(), e);
        }
    }
}
