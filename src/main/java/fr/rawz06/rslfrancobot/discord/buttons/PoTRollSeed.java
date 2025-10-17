package fr.rawz06.rslfrancobot.discord.buttons;

import fr.rawz06.rslfrancobot.discord.CommandExecutor;
import fr.rawz06.rslfrancobot.discord.annotations.DiscordButtonAction;
import fr.rawz06.rslfrancobot.discord.context.CommandContext;
import fr.rawz06.rslfrancobot.seed.GenerateSeed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@DiscordButtonAction(name = "pot")
@RequiredArgsConstructor
public class PoTRollSeed implements CommandExecutor {

    private final GenerateSeed generateSeed;

    @Override
    @SneakyThrows
    public void execute(CommandContext ctx) {
        ctx.replyWait();
        Thread.sleep(1000);
        String seed = generateSeed.generateSeed();
        ctx.reply("Your seed pot is generated", seed);
    }
}
