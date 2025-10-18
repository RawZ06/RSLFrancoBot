package fr.rawz06.rslfrancobot;

import fr.rawz06.rslfrancobot.api.discord.JDAEventListener;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Component responsible for initializing and starting the Discord bot.
 * Configures JDA and registers slash commands.
 */
@Component
public class BotRunner {

    private static final Logger logger = LoggerFactory.getLogger(BotRunner.class);

    @Value("${app.discord.token}")
    private String token;

    private final JDAEventListener eventListener;

    public BotRunner(JDAEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @PostConstruct
    public void init() {
        try {
            logger.info("Starting Discord bot...");

            JDA jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(eventListener)
                    .build();

            jda.awaitReady();

            // Register slash commands
            jda.updateCommands().addCommands(
                    Commands.slash("seed", "Generate an OoT Randomizer seed")
            ).queue();

            logger.info("Discord bot started successfully!");

        } catch (Exception e) {
            logger.error("Error starting bot", e);
            throw new RuntimeException("Unable to start Discord bot", e);
        }
    }
}
