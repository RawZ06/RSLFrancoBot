package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Handler for the PoT button.
 * Directly generates a PoT seed (no user options).
 */
@Component
public class PoTButtonHandler {

    private final SeedService seedService;
    private final SeedPresenter presenter;

    public PoTButtonHandler(SeedService seedService, SeedPresenter presenter) {
        this.seedService = seedService;
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        try {
            // Defer immediately as generation takes time (5s simulated)
            interaction.defer();

            // Generate seed (blocking, simulates slow HTTP call)
            SeedResult result = seedService.generateSeed(
                    SeedMode.POT,
                    interaction.getUserId(),
                    Map.of()
            );

            // Edit deferred reply with result
            interaction.editDeferredReply(presenter.presentSeedResult(result));
        } catch (SeedService.SeedGenerationException e) {
            interaction.editDeferredReply(presenter.presentError(e.getMessage()));
        }
    }
}
