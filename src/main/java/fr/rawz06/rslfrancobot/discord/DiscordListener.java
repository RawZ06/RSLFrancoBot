package fr.rawz06.rslfrancobot.discord;

import fr.rawz06.rslfrancobot.discord.context.CommandButtonContext;
import fr.rawz06.rslfrancobot.discord.context.CommandMessageContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DiscordListener extends ListenerAdapter {
    private final Map<String, CommandExecutor> commands;
    private final Map<String, CommandExecutor> buttons;
    private final String prefix;
    private static final Map<Long, List<String>> userSelections = new ConcurrentHashMap<>();

    public DiscordListener(
            @Value("${discord.prefix:!}") String prefix,
            PackageAnalyzer packageAnalyzer
    ) {
        this.prefix = prefix;
        this.commands = packageAnalyzer.analyzeCommands();
        this.buttons = packageAnalyzer.analyzeButtons();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String raw = event.getMessage().getContentRaw();
        log.info("Message received: {}", raw);
        if (!raw.startsWith(prefix) || event.getAuthor().isBot()) return;

        String[] parts = raw.substring(prefix.length()).split("\\s+");
        String cmdName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? java.util.Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        CommandExecutor command = commands.get(cmdName);
        if (command != null) {
            command.execute(new CommandMessageContext(event, args));
        } else {
            log.warn("Command not found: {}", cmdName);
            //event.getChannel().sendMessage("❓ Unknown command: `" + cmdName + "`").queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();
        log.info("ButtonInteraction received: {}", id);

        long userId = event.getUser().getIdLong();

        if (id.endsWith("_validate")) {
            List<String> selections = userSelections.getOrDefault(userId, List.of());

            CommandExecutor command = buttons.get("franco_selector_validate");
            if (command != null) {
                CommandButtonContext ctx = new CommandButtonContext(event);
                ctx.setSelections(selections);
                command.execute(ctx);
            }
            return;
        }

        CommandExecutor command = buttons.get(id);
        if (command != null) {
            command.execute(new CommandButtonContext(event));
        } else {
            log.warn("Button not found: {}", id);
            //event.getChannel().sendMessage("❓ Unknown button: `" + id + "`").queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String menuId = event.getComponentId();
        long userId = event.getUser().getIdLong();
        List<String> selectedValues = event.getValues();

        userSelections.compute(userId, (id, existing) -> {
            if (existing == null) existing = new ArrayList<>();
            existing.removeIf(v -> menuId.startsWith(v)); // supprime les anciennes valeurs de ce menu
            existing.addAll(selectedValues);
            return existing;
        });

        event.reply("✅ Sélection enregistrée pour `" + menuId + "` : " + selectedValues)
                .setEphemeral(true)
                .queue();
    }
}
