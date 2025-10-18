package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler pour le bouton Franco.
 * Affiche le menu de sélection des options Franco.
 */
@Component
public class FrancoButtonHandler {

    private final SeedService seedService;
    private final SeedPresenter presenter;

    public FrancoButtonHandler(SeedService seedService, SeedPresenter presenter) {
        this.seedService = seedService;
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        try {
            List<Preset.PresetOption> options = seedService.getAvailableOptions("franco");
            interaction.reply(presenter.presentFrancoOptions(options));
        } catch (Exception e) {
            interaction.reply(presenter.presentError("Impossible de charger les options Franco : " + e.getMessage()));
        }
    }
}
