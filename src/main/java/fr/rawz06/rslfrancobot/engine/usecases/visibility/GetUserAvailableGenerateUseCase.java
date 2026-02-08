package fr.rawz06.rslfrancobot.engine.usecases.visibility;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetUserAvailableGenerateUseCase {

    Map<String, List<String>> availability = Map.of("tot", List.of("rawz06", "barbu", "skols"));

    public boolean available(String user, String preset) {
        return availability.get(preset).contains(user);
    }
}
