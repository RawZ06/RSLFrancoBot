package fr.rawz06.rslfrancobot.bot.models;

/**
 * Represents a Discord button.
 */
public record DiscordButton(
        String label,
        String customId,
        Style style
) {
    public enum Style {
        PRIMARY,    // Blue
        SECONDARY,  // Gray
        SUCCESS,    // Green
        DANGER      // Red
    }

    public DiscordButton(String label, String customId) {
        this(label, customId, Style.PRIMARY);
    }
}
