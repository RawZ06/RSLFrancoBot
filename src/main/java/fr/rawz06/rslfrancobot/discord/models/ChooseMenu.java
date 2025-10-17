package fr.rawz06.rslfrancobot.discord.models;

import lombok.Builder;

import java.util.List;

@Builder
public record ChooseMenu(String name, String placeholder, Integer minValue, Integer maxValue,
                         List<ChooseMenuItem> options, String message) {

    @Builder
    public record ChooseMenuItem(String label, String description, String value) {
    }

}
