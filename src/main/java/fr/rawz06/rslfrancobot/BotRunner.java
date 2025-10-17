package fr.rawz06.rslfrancobot;

import fr.rawz06.rslfrancobot.discord.DiscordListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotRunner implements CommandLineRunner {

    @Value("${app.discord.token}")
    private String token;
    private final DiscordListener listener;

    @Override
    public void run(String... args) throws Exception {
        JDABuilder.createDefault(token)
                .addEventListeners(listener)
                .build();
    }
}
