package fr.rawz06.rslfrancobot.bot.handlers.std;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Handler for the Mixed Pool button.
 * Directly generates a Mixed Pool seed with fixed settings from mixed.json.
 */
@Component
public class MixedPoolButtonHandler {

    private final SeedService seedService;
    private final SeedPresenter presenter;

    public MixedPoolButtonHandler(SeedService seedService, SeedPresenter presenter) {
        this.seedService = seedService;
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        try {
            // Defer immediately as generation takes time
            interaction.defer();

            // Generate seed with fixed settings from mixed.json
            SeedResult result = seedService.generateSeed(
                    SeedMode.MIXED,
                    interaction.getUserId(),
                    Map.of()
            );

            // Send final result as channel message
            interaction.sendChannelMessage(presenter.presentSeedResult(result, "Mixed Pool S5", interaction.getUsername()));

            // Delete interaction messages to keep channel clean
            interaction.deleteOriginalMessage();
        } catch (SeedService.SeedGenerationException e) {
            interaction.editDeferredReply(presenter.presentError(e.getMessage()));
        }
    }
}
