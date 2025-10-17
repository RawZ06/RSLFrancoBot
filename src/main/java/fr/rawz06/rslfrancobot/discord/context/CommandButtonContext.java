package fr.rawz06.rslfrancobot.discord.context;

import fr.rawz06.rslfrancobot.discord.models.ChooseMenu;
import fr.rawz06.rslfrancobot.utils.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class CommandButtonContext implements CommandContext {
    private final ButtonInteractionEvent event;
    private InteractionHook hook;
    @Getter
    @Setter
    private List<String> selections = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        event.getChannel().sendMessage(message).queue();
    }

    @Override
    public void replyWait() {
        event.deferReply(true).queue(h -> this.hook = h);
    }

    @Override
    public void reply(String alert, String message) {
        if (hook != null) {
            hook.sendMessage(alert).setEphemeral(false).queue();
            event.getChannel().sendMessage(message).queue();
        } else {
            event.reply(message).queue();
        }
    }

    @Override
    public void sendMenu(ChooseMenu menu) {
        List<ChooseMenu.ChooseMenuItem> options = menu.options();
        List<ActionRow> rows = new ArrayList<>();

        int total = options.size();
        int chunkSize = 25;
        int index = 0;
        int menuIndex = 1;

        while (index < total && rows.size() < 4) { // on garde 1 ActionRow pour le bouton
            int end = Math.min(index + chunkSize, total);
            List<ChooseMenu.ChooseMenuItem> subList = options.subList(index, end);

            StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(menu.name() + "_page" + menuIndex)
                    .setPlaceholder(menu.placeholder() + " (page " + menuIndex + ")")
                    .setMinValues(menu.minValue())
                    .setMaxValues(menu.maxValue());

            for (ChooseMenu.ChooseMenuItem item : subList) {
                menuBuilder.addOption(item.label(), item.value(), item.description());
            }

            rows.add(ActionRow.of(menuBuilder.build()));
            index = end;
            menuIndex++;
        }

        // Ajout du bouton "Valider"
        Button validateButton = Button.success(menu.name() + "_validate", "✅ Valider la sélection");

        rows.add(ActionRow.of(validateButton));

        event.getChannel()
                .sendMessage(menu.message())
                .setComponents(rows)
                .queue();
    }

    @Override
    public void sendButtons(String message, List<Tuple<String, String>> buttons) {
        event.getChannel().sendMessage(message)
                .setComponents(ActionRow.of(
                        buttons.stream().map(button -> Button.primary(button.first(), button.second())).toList()
                )).queue();
    }

    @Override
    public String getAuthor() {
        return event.getUser().getName();
    }

    @Override
    public void sendFile(String message, String fileName, String response) {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        event.getChannel()
                .sendMessage(message)
                .addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(bytes, fileName))
                .queue();
    }
}
