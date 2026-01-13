package fr.rawz06.rslfrancobot.bot.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord selection menu (StringSelectMenu).
 */
@Getter
public class DiscordSelectMenu {
    private final String customId;
    @Setter
    private String placeholder;
    @Setter
    private int minValues = 1;
    @Setter
    private int maxValues = 1;
    private final List<SelectOption> options = new ArrayList<>();

    public DiscordSelectMenu(String customId) {
        this.customId = customId;
    }

    public void addOption(String label, String value, String description) {
        this.options.add(new SelectOption(label, value, description));
    }

    public record SelectOption(
            String label,
            String value,
            String description
    ) { }
}
