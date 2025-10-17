package fr.rawz06.rslfrancobot.discord.commands;

import fr.rawz06.rslfrancobot.discord.CommandExecutor;
import fr.rawz06.rslfrancobot.discord.annotations.DiscordCommand;
import fr.rawz06.rslfrancobot.discord.context.CommandContext;
import fr.rawz06.rslfrancobot.utils.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@DiscordCommand(name = "seed")
@Component
@Slf4j
public class Seed implements CommandExecutor {
    @Override
    public void execute(CommandContext ctx) {
        log.info("Executing Seed Command by {}", ctx.getAuthor());
        ctx.sendButtons("Choose your seed",
                List.of(
                        new Tuple<>("rsl", "RSL"),
                        new Tuple<>("franco", "Franco"),
                        new Tuple<>("pot", "PoT")
                ));
    }
}
