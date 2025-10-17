package fr.rawz06.rslfrancobot.discord.buttons;

import fr.rawz06.rslfrancobot.discord.CommandExecutor;
import fr.rawz06.rslfrancobot.discord.annotations.DiscordButtonAction;
import fr.rawz06.rslfrancobot.discord.context.CommandContext;
import fr.rawz06.rslfrancobot.franco.FrancoOptionBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@DiscordButtonAction(name = "franco")
@RequiredArgsConstructor
public class FrancoAnswer implements CommandExecutor {

    private final FrancoOptionBuilder builder;

    @Override
    @SneakyThrows
    public void execute(CommandContext ctx) {
        ctx.sendMenu(builder.build());
    }
}
