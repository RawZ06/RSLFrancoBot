package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for Franco options validation.
 * Retrieves user selections and generates the seed.
 */
@Component
public class FrancoValidateHandler {

    private final SeedService seedService;
    private final SeedPresenter presenter;

    public FrancoValidateHandler(SeedService seedService, SeedPresenter presenter) {
        this.seedService = seedService;
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        // Defer immediately to avoid Discord timeout
        interaction.defer();

        try {
            // Retrieve stored selections
            @SuppressWarnings("unchecked")
            List<String> selectedOptions = (List<String>) interaction.getUserData("franco_selections", List.class);

            if (selectedOptions == null) {
                selectedOptions = List.of();
            }

            // Convert to Map for SeedService (option IDs as keys)
            Map<String, String> userSettings = new HashMap<>();
            for (String optionId : selectedOptions) {
                userSettings.put(optionId, "true");
            }

            // Generate seed
            SeedResult result = seedService.generateSeed(
                    SeedMode.FRANCO,
                    interaction.getUserId(),
                    userSettings
            );

            // Send final result as channel message (persists after cleanup)
            interaction.sendChannelMessage(presenter.presentFrancoSeedResult(result, selectedOptions));

            // Delete interaction messages to keep channel clean
            interaction.deleteOriginalMessage();
        } catch (SeedService.SeedGenerationException e) {
            interaction.editDeferredReply(presenter.presentError(e.getMessage()));
        }
    }
}
