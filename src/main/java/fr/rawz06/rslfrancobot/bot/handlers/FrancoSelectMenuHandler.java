package fr.rawz06.rslfrancobot.bot.handlers;

import fr.rawz06.rslfrancobot.bot.models.DiscordInteraction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for selections in Franco menus.
 * Stores user selections.
 */
@Component
public class FrancoSelectMenuHandler {

    public void handle(DiscordInteraction interaction) {
        List<String> selectedValues = interaction.getSelectedValues();

        // Retrieve previous selections
        @SuppressWarnings("unchecked")
        List<String> existingSelections = (List<String>) interaction.getUserData("franco_selections", List.class);
        if (existingSelections == null) {
            existingSelections = new ArrayList<>();
        }

        // Add new selections (avoiding duplicates)
        for (String value : selectedValues) {
            if (!existingSelections.contains(value)) {
                existingSelections.add(value);
            }
        }

        // Store updated selections
        interaction.storeUserData("franco_selections", existingSelections);

        // Silently acknowledge the interaction (no visible message)
        interaction.acknowledgeSelect();
    }
}
