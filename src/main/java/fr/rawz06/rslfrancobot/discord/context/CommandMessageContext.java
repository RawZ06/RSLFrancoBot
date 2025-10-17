package fr.rawz06.rslfrancobot.discord.context;

import fr.rawz06.rslfrancobot.discord.models.ChooseMenu;
import fr.rawz06.rslfrancobot.utils.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class CommandMessageContext implements CommandContext {
    private final MessageReceivedEvent event;
    @Getter
    private final String[] args;

    @Override
    public void sendMessage(String message) {
        event.getChannel().sendMessage(message).queue();
    }

    @Override
    public final void sendButtons(String message, List<Tuple<String, String>> buttons) {
        event.getChannel().sendMessage(message)
                .setComponents(ActionRow.of(
                        buttons.stream().map(button -> Button.primary(button.first(), button.second())).toList()
                )).queue();
    }

    @Override
    public String getAuthor() {
        return event.getAuthor().getName();
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
