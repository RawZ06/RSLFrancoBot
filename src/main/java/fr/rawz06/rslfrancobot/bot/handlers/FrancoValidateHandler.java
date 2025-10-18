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
 * Handler pour la validation des options Franco.
 * Récupère les sélections de l'utilisateur et génère la seed.
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
        // Defer immédiatement pour éviter le timeout Discord
        interaction.defer();

        try {
            // Récupérer les sélections stockées
            @SuppressWarnings("unchecked")
            List<String> selectedOptions = (List<String>) interaction.getUserData("franco_selections", List.class);

            if (selectedOptions == null) {
                selectedOptions = List.of();
            }

            // Afficher les options sélectionnées pour rassurer l'utilisateur
            interaction.editDeferredReply(presenter.presentSelectedOptions(selectedOptions));

            // Attendre un peu pour que l'utilisateur voie les options
            Thread.sleep(2000);

            // Convertir en Map pour SeedService (les IDs des options comme clés)
            Map<String, String> userSettings = new HashMap<>();
            for (String optionId : selectedOptions) {
                userSettings.put(optionId, "true");
            }

            // Générer la seed
            SeedResult result = seedService.generateSeed(
                    SeedMode.FRANCO,
                    interaction.getUserId(),
                    userSettings
            );

            // Éditer avec le résultat final
            interaction.editDeferredReply(presenter.presentSeedResult(result));
        } catch (SeedService.SeedGenerationException e) {
            interaction.editDeferredReply(presenter.presentError(e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            interaction.editDeferredReply(presenter.presentError("Génération interrompue"));
        }
    }
}
