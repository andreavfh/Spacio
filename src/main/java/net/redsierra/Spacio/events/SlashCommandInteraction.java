package net.redsierra.Spacio.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public record SlashCommandInteraction(SlashCommandInteractionEvent event) {


    public Guild getGuild() {
        return event.getGuild();
    }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }




}