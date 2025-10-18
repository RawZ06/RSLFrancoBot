package fr.rawz06.rslfrancobot.bot.models;

import java.util.List;
import java.util.Map;

/**
 * Représente une interaction Discord abstraite.
 * Découplée de JDA, permet de tester les handlers sans dépendre de Discord.
 */
public interface DiscordInteraction {
    /**
     * @return L'ID de l'utilisateur qui a déclenché l'interaction
     */
    String getUserId();

    /**
     * @return Le nom d'utilisateur Discord
     */
    String getUsername();

    /**
     * @return L'ID du channel Discord
     */
    String getChannelId();

    /**
     * @return Les valeurs sélectionnées dans un menu (si applicable)
     */
    List<String> getSelectedValues();

    /**
     * @return Les données custom attachées au bouton/interaction (si applicable)
     */
    String getCustomId();

    /**
     * Répond à l'interaction avec un message.
     */
    void reply(DiscordMessage message);

    /**
     * Répond en mode "defer" puis met à jour avec un message.
     */
    void deferAndReply(DiscordMessage message);

    /**
     * Defer la réponse (affiche "Bot is thinking...").
     * Utilisé quand le traitement va prendre du temps.
     */
    void defer();

    /**
     * Édite la réponse différée après un defer().
     */
    void editDeferredReply(DiscordMessage message);

    /**
     * Envoie un fichier en réponse.
     */
    void sendFile(String filename, byte[] content, String mimeType);

    /**
     * Stocke temporairement des données pour cet utilisateur.
     */
    void storeUserData(String key, Object value);

    /**
     * Récupère des données stockées pour cet utilisateur.
     */
    <T> T getUserData(String key, Class<T> type);

    /**
     * Obtient toutes les données utilisateur stockées.
     */
    Map<String, Object> getAllUserData();
}
