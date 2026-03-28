package fr.rawz06.rslfrancobot.bot.handlers.rsl;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Handler for the RSL Season 8 button.
 */
@Component
public class RSLS8ButtonHandler {

    private final SeedService seedService;
    private final SeedPresenter presenter;

    public RSLS8ButtonHandler(SeedService seedService, SeedPresenter presenter) {
        this.seedService = seedService;
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        try {
            interaction.defer();

            SeedResult result = seedService.generateSeed(
                    SeedMode.RSL_SEASON8,
                    interaction.getUserId(),
                    Map.of()
            );

            interaction.sendChannelMessage(presenter.presentSeedResult(result, "S8 (RSL)", interaction.getUsername()));
            interaction.deleteOriginalMessage();
        } catch (SeedService.SeedGenerationException e) {
            interaction.editDeferredReply(presenter.presentError(e.getMessage()));
        }
    }
}
