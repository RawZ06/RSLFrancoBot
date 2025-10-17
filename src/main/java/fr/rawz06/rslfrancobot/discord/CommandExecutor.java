package fr.rawz06.rslfrancobot.discord;

import fr.rawz06.rslfrancobot.discord.context.CommandContext;

public interface CommandExecutor {
    void execute(CommandContext ctx);
}
