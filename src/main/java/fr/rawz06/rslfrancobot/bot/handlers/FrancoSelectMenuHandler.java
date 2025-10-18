package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler pour les sélections dans les menus Franco.
 * Stocke les sélections de l'utilisateur.
 */
@Component
public class FrancoSelectMenuHandler {

    public void handle(DiscordInteraction interaction) {
        List<String> selectedValues = interaction.getSelectedValues();

        // Récupérer les sélections précédentes
        @SuppressWarnings("unchecked")
        List<String> existingSelections = (List<String>) interaction.getUserData("franco_selections", List.class);
        if (existingSelections == null) {
            existingSelections = new ArrayList<>();
        }

        // Ajouter les nouvelles sélections (en évitant les doublons)
        for (String value : selectedValues) {
            if (!existingSelections.contains(value)) {
                existingSelections.add(value);
            }
        }

        // Stocker les sélections mises à jour
        interaction.storeUserData("franco_selections", existingSelections);

        // Accuser réception (message éphémère)
        interaction.reply(new fr.rawz06.rslfrancobot.bot.models.DiscordMessage(
                "✅ Sélection enregistrée ! Clique sur 'Valider et générer' quand tu es prêt."
        ) {{
            setEphemeral(true);
        }});
    }
}
