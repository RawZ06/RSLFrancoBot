package fr.rawz06.rslfrancobot.discord.context;

import fr.rawz06.rslfrancobot.discord.models.ChooseMenu;
import fr.rawz06.rslfrancobot.utils.Tuple;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface CommandContext {
    void sendMessage(String message);
    default void replyWait() {

    }

    default void reply(String alert, String message) {
        sendMessage(message);
    }

    default void replyAfter(String message) {
        sendMessage(message);
    }

    default void sendMenu(ChooseMenu menu) {

    }

    void sendButtons(String message, List<Tuple<String, String>> buttons);
    String getAuthor();

    default List<String> getSelections() {
        return Collections.emptyList();
    }

    void sendFile(String message, String fileName, String response);
}
