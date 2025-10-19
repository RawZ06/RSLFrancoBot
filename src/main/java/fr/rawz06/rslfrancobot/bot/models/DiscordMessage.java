package fr.rawz06.rslfrancobot.bot.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord message to send.
 * Abstraction decoupled from JDA.
 */
public class DiscordMessage {
    private String content;
    private final List<DiscordButton> buttons = new ArrayList<>();
    private final List<List<DiscordButton>> buttonRows = new ArrayList<>();
    private final List<DiscordSelectMenu> selectMenus = new ArrayList<>();
    private boolean ephemeral = false;

    public DiscordMessage(String content) {
        this.content = content;
    }

    public DiscordMessage() {
        this("");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<DiscordButton> getButtons() {
        return buttons;
    }

    public void addButton(DiscordButton button) {
        this.buttons.add(button);
    }

    public void addButton(String label, String customId) {
        this.buttons.add(new DiscordButton(label, customId, DiscordButton.Style.PRIMARY));
    }

    public void addButton(String label, String customId, DiscordButton.Style style) {
        this.buttons.add(new DiscordButton(label, customId, style));
    }

    public List<List<DiscordButton>> getButtonRows() {
        return buttonRows;
    }

    public void addButtonRow(List<DiscordButton> row) {
        this.buttonRows.add(row);
    }

    public List<DiscordSelectMenu> getSelectMenus() {
        return selectMenus;
    }

    public void addSelectMenu(DiscordSelectMenu menu) {
        this.selectMenus.add(menu);
    }

    public boolean isEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }
}
