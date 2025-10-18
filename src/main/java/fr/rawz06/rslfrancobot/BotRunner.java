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
 * Composant responsable de l'initialisation et du démarrage du bot Discord.
 * Configure JDA et enregistre les commandes slash.
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
            logger.info("Démarrage du bot Discord...");

            JDA jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(eventListener)
                    .build();

            jda.awaitReady();

            // Enregistrer les commandes slash
            jda.updateCommands().addCommands(
                    Commands.slash("seed", "Générer une seed OoT Randomizer")
            ).queue();

            logger.info("Bot Discord démarré avec succès !");

        } catch (Exception e) {
            logger.error("Erreur lors du démarrage du bot", e);
            throw new RuntimeException("Impossible de démarrer le bot Discord", e);
        }
    }
}
