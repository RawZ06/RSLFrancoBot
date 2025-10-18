package fr.rawz06.rslfrancobot.bot.models;

import java.util.List;
import java.util.Map;

/**
 * Represents an abstract Discord interaction.
 * Decoupled from JDA, allows testing handlers without depending on Discord.
 */
public interface DiscordInteraction {
    /**
     * @return The ID of the user who triggered the interaction
     */
    String getUserId();

    /**
     * @return The Discord username
     */
    String getUsername();

    /**
     * @return The Discord channel ID
     */
    String getChannelId();

    /**
     * @return Values selected in a menu (if applicable)
     */
    List<String> getSelectedValues();

    /**
     * @return Custom data attached to the button/interaction (if applicable)
     */
    String getCustomId();

    /**
     * Responds to the interaction with a message.
     */
    void reply(DiscordMessage message);

    /**
     * Responds in "defer" mode then updates with a message.
     */
    void deferAndReply(DiscordMessage message);

    /**
     * Defers the response (displays "Bot is thinking...").
     * Used when processing will take time.
     */
    void defer();

    /**
     * Edits the deferred response after a defer().
     */
    void editDeferredReply(DiscordMessage message);

    /**
     * Sends a file in response.
     */
    void sendFile(String filename, byte[] content, String mimeType);

    /**
     * Temporarily stores data for this user.
     */
    void storeUserData(String key, Object value);

    /**
     * Retrieves stored data for this user.
     */
    <T> T getUserData(String key, Class<T> type);

    /**
     * Gets all stored user data.
     */
    Map<String, Object> getAllUserData();
}
