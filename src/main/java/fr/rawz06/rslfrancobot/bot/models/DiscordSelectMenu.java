package fr.rawz06.rslfrancobot.bot.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord selection menu (StringSelectMenu).
 */
public class DiscordSelectMenu {
    private String customId;
    private String placeholder;
    private int minValues = 1;
    private int maxValues = 1;
    private final List<SelectOption> options = new ArrayList<>();

    public DiscordSelectMenu(String customId) {
        this.customId = customId;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public int getMinValues() {
        return minValues;
    }

    public void setMinValues(int minValues) {
        this.minValues = minValues;
    }

    public int getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(int maxValues) {
        this.maxValues = maxValues;
    }

    public List<SelectOption> getOptions() {
        return options;
    }

    public void addOption(SelectOption option) {
        this.options.add(option);
    }

    public void addOption(String label, String value, String description) {
        this.options.add(new SelectOption(label, value, description));
    }

    public record SelectOption(
            String label,
            String value,
            String description
    ) {
        public SelectOption(String label, String value) {
            this(label, value, null);
        }
    }
}
