package fr.rawz06.rslfrancobot.seed;

import org.springframework.stereotype.Component;

@Component
public class MockSeed implements GenerateSeed {
    @Override
    public String generateSeed() {
        return "https://ootrandomizer.com/seed/get?id=1981210";
    }
}
