package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import fr.rawz06.rslfrancobot.bot.presenters.SeedPresenter;
import fr.rawz06.rslfrancobot.bot.services.SeedService;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedMode;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Handler pour le bouton RSL.
 * Génère directement une seed RSL (pas d'options utilisateur).
 */
@Component
public class RSLButtonHandler {

    private final SeedService seedService;
    private final SeedPresenter presenter;

    public RSLButtonHandler(SeedService seedService, SeedPresenter presenter) {
        this.seedService = seedService;
        this.presenter = presenter;
    }

    public void handle(DiscordInteraction interaction) {
        try {
            // Defer immédiatement car la génération prend du temps (5s simulées)
            interaction.defer();

            // Générer la seed (bloquant, simule un appel HTTP lent)
            SeedResult result = seedService.generateSeed(
                    SeedMode.RSL,
                    interaction.getUserId(),
                    Map.of()
            );

            // Éditer la réponse différée avec le résultat
            interaction.editDeferredReply(presenter.presentSeedResult(result));
        } catch (SeedService.SeedGenerationException e) {
            interaction.editDeferredReply(presenter.presentError(e.getMessage()));
        }
    }
}
