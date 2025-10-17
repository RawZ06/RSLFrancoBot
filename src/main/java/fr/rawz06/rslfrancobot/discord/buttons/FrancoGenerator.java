package fr.rawz06.rslfrancobot.discord.buttons;

import fr.rawz06.rslfrancobot.discord.CommandExecutor;
import fr.rawz06.rslfrancobot.discord.annotations.DiscordButtonAction;
import fr.rawz06.rslfrancobot.discord.context.CommandContext;
import fr.rawz06.rslfrancobot.franco.SettingParser;
import fr.rawz06.rslfrancobot.seed.GenerateSeed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DiscordButtonAction(name = "franco_selector_validate")
@RequiredArgsConstructor
public class FrancoGenerator implements CommandExecutor {

    private final GenerateSeed generateSeed;
    private final SettingParser parser;

    @SneakyThrows
    @Override
    public void execute(CommandContext ctx) {
        ctx.replyWait();
        String seed = generateSeed.generateSeed();
        Thread.sleep(1000);
        String response = parser.parse(ctx.getSelections());
        ctx.reply("Your seed is generated with " + ctx.getSelections(), "Success");
        ctx.sendFile("Your settings", "settings.json", response);
    }
}
