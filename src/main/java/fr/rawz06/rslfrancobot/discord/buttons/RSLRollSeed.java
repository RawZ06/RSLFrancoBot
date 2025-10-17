package fr.rawz06.rslfrancobot.discord.buttons;

import fr.rawz06.rslfrancobot.discord.CommandExecutor;
import fr.rawz06.rslfrancobot.discord.annotations.DiscordButtonAction;
import fr.rawz06.rslfrancobot.discord.context.CommandContext;
import fr.rawz06.rslfrancobot.seed.GenerateSeed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@DiscordButtonAction(name = "rsl")
@RequiredArgsConstructor
public class RSLRollSeed implements CommandExecutor {

    private final GenerateSeed generateSeed;

    @Override
    @SneakyThrows
    public void execute(CommandContext ctx) {
        ctx.replyWait();
        Thread.sleep(1000);
        String seed = generateSeed.generateSeed();
        ctx.reply("Your seed RSL is generated", seed);
    }
}
