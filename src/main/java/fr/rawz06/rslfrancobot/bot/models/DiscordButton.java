package fr.rawz06.rslfrancobot.bot.models;

/**
 * Représente un bouton Discord.
 */
public record DiscordButton(
        String label,
        String customId,
        Style style
) {
    public enum Style {
        PRIMARY,    // Bleu
        SECONDARY,  // Gris
        SUCCESS,    // Vert
        DANGER      // Rouge
    }

    public DiscordButton(String label, String customId) {
        this(label, customId, Style.PRIMARY);
    }
}
