package fr.rawz06.rslfrancobot.discord;

import fr.rawz06.rslfrancobot.discord.annotations.DiscordButtonAction;
import fr.rawz06.rslfrancobot.discord.annotations.DiscordCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PackageAnalyzer {

    private final ApplicationContext context;
    @Value("${discord.prefix:!}") private String prefix;

    public Map<String, CommandExecutor> analyzeCommands() {
        Map<String, CommandExecutor> commands = new HashMap<>();
        Map<String, Object> beans = context.getBeansWithAnnotation(DiscordCommand.class);
        for (Object bean : beans.values()) {
            DiscordCommand annotation = bean.getClass().getAnnotation(DiscordCommand.class);
            if (bean instanceof CommandExecutor executor) {
                commands.put(annotation.name().toLowerCase(), executor);
                log.info("✅ Registered command: {}{}", prefix, annotation.name());
            }
        }
        return commands;
    }

    public Map<String, CommandExecutor> analyzeButtons() {
        Map<String, CommandExecutor> commands = new HashMap<>();
        Map<String, Object> beans = context.getBeansWithAnnotation(DiscordButtonAction.class);
        for (Object bean : beans.values()) {
            DiscordButtonAction annotation = bean.getClass().getAnnotation(DiscordButtonAction.class);
            if (bean instanceof CommandExecutor executor) {
                commands.put(annotation.name().toLowerCase(), executor);
                log.info("✅ Registered button: {}", annotation.name());
            }
        }
        return commands;
    }
}
