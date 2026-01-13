package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import org.springframework.stereotype.Component;

/**
 * Handler for the /seed command.
 * Displays mode selection buttons.
 */
@Component
public class SaladCommandHandler {

    private final SeedPresenter presenter;

    public SaladCommandHandler(SeedPresenter presenter) {
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        interaction.reply(presenter.presentModeSaladSelection());
    }
}
