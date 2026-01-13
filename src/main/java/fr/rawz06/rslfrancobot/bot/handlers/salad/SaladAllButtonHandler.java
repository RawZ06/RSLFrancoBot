package fr.rawz06.rslfrancobot.bot.handlers.salad;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Handler for the Mix button.
 * Directly generates an Mix seed with fixed settings from salad.json (no user options).
 */
@Component
public class SaladAllButtonHandler {

    private final SeedService seedService;
    private final SeedPresenter presenter;

    public SaladAllButtonHandler(SeedService seedService, SeedPresenter presenter) {
        this.seedService = seedService;
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        try {
            // Defer immediately as generation takes time
            interaction.defer();

            // Generate seed with fixed settings from s9.json
            SeedResult result = seedService.generateSeed(
                    SeedMode.SALAD_ALL,
                    interaction.getUserId(),
                    Map.of()
            );

            // Send final result as channel message (persists after cleanup)
            interaction.sendChannelMessage(presenter.presentSeedResult(result, "Salade en folie", interaction.getUsername()));

            // Delete interaction messages to keep channel clean
            interaction.deleteOriginalMessage();
        } catch (SeedService.SeedGenerationException e) {
            interaction.editDeferredReply(presenter.presentError(e.getMessage()));
        }
    }
}
