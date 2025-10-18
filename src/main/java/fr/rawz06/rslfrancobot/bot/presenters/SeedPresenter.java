package fr.rawz06.rslfrancobot.bot.presenters;

import fr.rawz06.rslfrancobot.bot.models.DiscordButton;
import fr.rawz06.rslfrancobot.bot.models.DiscordMessage;
import fr.rawz06.rslfrancobot.bot.models.DiscordSelectMenu;
import fr.rawz06.rslfrancobot.engine.domain.entities.Preset;
import fr.rawz06.rslfrancobot.engine.domain.entities.SeedResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Presenter pour formater les messages liés aux seeds.
 * Transforme les objets du domaine en messages Discord.
 */
@Component
public class SeedPresenter {

    /**
     * Crée le message initial de choix du mode de seed.
     */
    public DiscordMessage presentModeSelection() {
        DiscordMessage message = new DiscordMessage("Quel type de seed veux-tu générer ?");
        message.addButton("Franco", "seed_franco", DiscordButton.Style.PRIMARY);
        message.addButton("RSL", "seed_rsl", DiscordButton.Style.SUCCESS);
        message.addButton("PoT", "seed_pot", DiscordButton.Style.DANGER);
        return message;
    }

    /**
     * Présente le résultat d'une seed générée.
     */
    public DiscordMessage presentSeedResult(SeedResult result) {
        StringBuilder content = new StringBuilder();
        content.append("✅ Seed générée avec succès !\n\n");
        content.append("🔗 Lien : ").append(result.seedUrl()).append("\n");

        if (result.seedHash() != null && !result.seedHash().isEmpty()) {
            content.append("🔑 Hash : ").append(result.seedHash());
        }

        return new DiscordMessage(content.toString());
    }

    /**
     * Présente un message d'erreur.
     */
    public DiscordMessage presentError(String errorMessage) {
        return new DiscordMessage("❌ Erreur : " + errorMessage);
    }

    /**
     * Présente le menu de sélection des options Franco.
     */
    public DiscordMessage presentFrancoOptions(List<Preset.PresetOption> options) {
        DiscordMessage message = new DiscordMessage(
                "Choisis les options Franco que tu veux activer :\n" +
                "(Tu peux en sélectionner plusieurs)"
        );

        // Découper les options en plusieurs menus si nécessaire (max 25 par menu)
        int menuIndex = 0;
        for (int i = 0; i < options.size(); i += 25) {
            List<Preset.PresetOption> chunk = options.subList(i, Math.min(i + 25, options.size()));
            DiscordSelectMenu menu = createOptionsMenu(chunk, menuIndex++);
            message.addSelectMenu(menu);
        }

        // Ajouter un bouton de validation
        message.addButton("Valider et générer", "franco_validate", DiscordButton.Style.SUCCESS);
        message.addButton("Annuler", "franco_cancel", DiscordButton.Style.SECONDARY);

        return message;
    }

    private DiscordSelectMenu createOptionsMenu(List<Preset.PresetOption> options, int menuIndex) {
        DiscordSelectMenu menu = new DiscordSelectMenu("franco_options_" + menuIndex);
        menu.setPlaceholder("Sélectionne tes options...");
        menu.setMinValues(0);
        menu.setMaxValues(options.size());

        for (Preset.PresetOption option : options) {
            menu.addOption(option.label(), option.id(), option.description());
        }

        return menu;
    }

    /**
     * Message de confirmation pour RSL/PoT.
     */
    public DiscordMessage presentRSLConfirmation(String mode) {
        return new DiscordMessage(
                String.format("🎲 Génération d'une seed %s aléatoire en cours...", mode)
        );
    }

    /**
     * Présente les options sélectionnées par l'utilisateur avant la génération.
     */
    public DiscordMessage presentSelectedOptions(List<String> selectedOptionIds) {
        StringBuilder content = new StringBuilder();
        content.append("🔧 Options Franco sélectionnées :\n\n");

        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            content.append("_Aucune option spécifique (preset de base uniquement)_\n");
        } else {
            for (String optionId : selectedOptionIds) {
                content.append("✅ ").append(optionId).append("\n");
            }
        }

        content.append("\n⏳ Génération de la seed en cours...");

        return new DiscordMessage(content.toString());
    }
}
