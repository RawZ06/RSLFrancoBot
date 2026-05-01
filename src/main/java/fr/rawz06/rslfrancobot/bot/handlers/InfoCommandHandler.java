package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.InfoPresenter;
import org.springframework.stereotype.Component;

@Component
public class InfoCommandHandler {

    private final InfoPresenter presenter;

    public InfoCommandHandler(InfoPresenter presenter) {
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        interaction.reply(presenter.presentInfo());
    }
}
