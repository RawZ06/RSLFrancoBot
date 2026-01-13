package fr.rawz06.rslfrancobot.bot.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord message to send.
 * Abstraction decoupled from JDA.
 */
@Getter
public class DiscordMessage {
    private final String content;
    private final List<DiscordButton> buttons = new ArrayList<>();
    private final List<List<DiscordButton>> buttonRows = new ArrayList<>();
    private final List<DiscordSelectMenu> selectMenus = new ArrayList<>();
    @Setter
    private boolean ephemeral = false;

    public DiscordMessage(String content) {
        this.content = content;
    }

    public DiscordMessage() {
        this("");
    }

    public void addButton(String label, String customId, DiscordButton.Style style) {
        this.buttons.add(new DiscordButton(label, customId, style));
    }

    public void addButtonRow(List<DiscordButton> row) {
        this.buttonRows.add(row);
    }

    public void addSelectMenu(DiscordSelectMenu menu) {
        this.selectMenus.add(menu);
    }

}
