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
     * Creates the initial seed mode selection message.
     */
    public DiscordMessage presentModeSelection() {
        DiscordMessage message = new DiscordMessage("What type of seed do you want to generate?");
        message.addButton("Franco", "seed_franco", DiscordButton.Style.PRIMARY);
        message.addButton("RSL", "seed_rsl", DiscordButton.Style.SUCCESS);
        message.addButton("PoT", "seed_pot", DiscordButton.Style.DANGER);
        return message;
    }

    /**
     * Presents the result of a generated seed (RSL/PoT mode).
     */
    public DiscordMessage presentSeedResult(SeedResult result, String seedType) {
        StringBuilder content = new StringBuilder();
        content.append("✅ Seed ").append(seedType).append(" generated successfully!\n\n");
        content.append("🔗 Link: ").append(result.seedUrl()).append("\n");

        if (result.seedHash() != null && !result.seedHash().isEmpty()) {
            content.append("🔑 Hash: ").append(result.seedHash());
        }

        return new DiscordMessage(content.toString());
    }

    /**
     * Presents the result of a generated Franco seed with selected options.
     */
    public DiscordMessage presentFrancoSeedResult(SeedResult result, List<String> selectedOptions) {
        StringBuilder content = new StringBuilder();
        content.append("✅ Seed Franco generated successfully!\n\n");
        content.append("🔗 Link: ").append(result.seedUrl()).append("\n");

        if (result.seedHash() != null && !result.seedHash().isEmpty()) {
            content.append("🔑 Hash: ").append(result.seedHash()).append("\n");
        }

        // Add selected settings
        content.append("\n🔧 **Settings used:**\n");
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            content.append("_Base preset only (no specific options)_");
        } else {
            for (String optionId : selectedOptions) {
                content.append("• ").append(optionId).append("\n");
            }
        }

        return new DiscordMessage(content.toString());
    }

    /**
     * Presents an error message.
     */
    public DiscordMessage presentError(String errorMessage) {
        return new DiscordMessage("❌ Error: " + errorMessage);
    }

    /**
     * Presents the Franco options selection menu.
     */
    public DiscordMessage presentFrancoOptions(List<Preset.PresetOption> options) {
        DiscordMessage message = new DiscordMessage(
                "Choose the Franco options you want to enable:\n" +
                "(You can select multiple options)"
        );

        // Split options into multiple menus if needed (max 25 per menu)
        int menuIndex = 0;
        for (int i = 0; i < options.size(); i += 25) {
            List<Preset.PresetOption> chunk = options.subList(i, Math.min(i + 25, options.size()));
            DiscordSelectMenu menu = createOptionsMenu(chunk, menuIndex++);
            message.addSelectMenu(menu);
        }

        // Add validation buttons
        message.addButton("Validate & Generate", "franco_validate", DiscordButton.Style.SUCCESS);
        message.addButton("Random Selection", "franco_random", DiscordButton.Style.PRIMARY);
        message.addButton("Cancel", "franco_cancel", DiscordButton.Style.SECONDARY);

        return message;
    }

    private DiscordSelectMenu createOptionsMenu(List<Preset.PresetOption> options, int menuIndex) {
        DiscordSelectMenu menu = new DiscordSelectMenu("franco_options_" + menuIndex);
        menu.setPlaceholder("Select your options...");
        menu.setMinValues(0);
        menu.setMaxValues(options.size());

        for (Preset.PresetOption option : options) {
            menu.addOption(option.label(), option.id(), option.description());
        }

        return menu;
    }

    /**
     * Confirmation message for RSL/PoT generation.
     */
    public DiscordMessage presentRSLConfirmation(String mode) {
        return new DiscordMessage(
                String.format("🎲 Generating a random %s seed...", mode)
        );
    }

    /**
     * Presents the selected options before seed generation.
     */
    public DiscordMessage presentSelectedOptions(List<String> selectedOptionIds) {
        StringBuilder content = new StringBuilder();
        content.append("🔧 Selected Franco options:\n\n");

        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            content.append("_No specific options (base preset only)_\n");
        } else {
            for (String optionId : selectedOptionIds) {
                content.append("✅ ").append(optionId).append("\n");
            }
        }

        content.append("\n⏳ Generating seed...");

        return new DiscordMessage(content.toString());
    }
}
