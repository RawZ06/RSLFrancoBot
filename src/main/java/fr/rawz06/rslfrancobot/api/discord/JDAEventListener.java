package fr.rawz06.rslfrancobot.api.discord;

import fr.rawz06.rslfrancobot.bot.handlers.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Listener JDA qui route les événements Discord vers les handlers appropriés.
 * Fait le pont entre JDA et notre architecture clean.
 */
@Component
public class JDAEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(JDAEventListener.class);

    private final SeedCommandHandler seedCommandHandler;
    private final FrancoButtonHandler francoButtonHandler;
    private final RSLButtonHandler rslButtonHandler;
    private final PoTButtonHandler potButtonHandler;
    private final FrancoValidateHandler francoValidateHandler;
    private final FrancoSelectMenuHandler francoSelectMenuHandler;

    public JDAEventListener(
            SeedCommandHandler seedCommandHandler,
            FrancoButtonHandler francoButtonHandler,
            RSLButtonHandler rslButtonHandler,
            PoTButtonHandler potButtonHandler,
            FrancoValidateHandler francoValidateHandler,
            FrancoSelectMenuHandler francoSelectMenuHandler
    ) {
        this.seedCommandHandler = seedCommandHandler;
        this.francoButtonHandler = francoButtonHandler;
        this.rslButtonHandler = rslButtonHandler;
        this.potButtonHandler = potButtonHandler;
        this.francoValidateHandler = francoValidateHandler;
        this.francoSelectMenuHandler = francoSelectMenuHandler;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        logger.info("Commande reçue : {} par {}", commandName, event.getUser().getName());

        try {
            if ("seed".equals(commandName)) {
                var interaction = JDASlashCommandAdapter.from(event);
                seedCommandHandler.handle(interaction);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de la commande : {}", commandName, e);
            event.reply("❌ Une erreur est survenue lors du traitement de la commande.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        logger.info("Bouton cliqué : {} par {}", buttonId, event.getUser().getName());

        try {
            var interaction = JDAInteractionAdapter.fromButtonEvent(event);

            switch (buttonId) {
                case "seed_franco" -> francoButtonHandler.handle(interaction);
                case "seed_rsl" -> rslButtonHandler.handle(interaction);
                case "seed_pot" -> potButtonHandler.handle(interaction);
                case "franco_validate" -> francoValidateHandler.handle(interaction);
                case "franco_cancel" -> event.reply("Génération annulée.").setEphemeral(true).queue();
                default -> event.reply("Bouton inconnu : " + buttonId).setEphemeral(true).queue();
            }
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du bouton : {}", buttonId, e);
            event.reply("❌ Une erreur est survenue.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String menuId = event.getComponentId();
        logger.info("Menu sélectionné : {} par {}", menuId, event.getUser().getName());

        try {
            var interaction = JDAInteractionAdapter.fromSelectMenuEvent(event);

            if (menuId.startsWith("franco_options_")) {
                francoSelectMenuHandler.handle(interaction);
            } else {
                event.reply("Menu inconnu : " + menuId).setEphemeral(true).queue();
            }
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du menu : {}", menuId, e);
            event.reply("❌ Une erreur est survenue.").setEphemeral(true).queue();
        }
    }
}
