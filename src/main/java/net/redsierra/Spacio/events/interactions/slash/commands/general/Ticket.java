package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

public class Ticket extends Command {

    public Ticket() {
        super.setName("ticket");
        super.setDescription("Creates a ticket.");
        super.setCategory("info");
    }
    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        SlashCommandInteractionEvent event = commandEvent.event();

        event.reply("Please select the reason of your ticket")
                .addActionRow(
                        StringSelectMenu.create("ticket-type")
                                .addOptions(SelectOption.of("Bug Report", "bug-report")
                                        .withDescription("Report a bug in the bot or in the server")
                                        .withEmoji(Emoji.fromUnicode("\ud83e\udd16")))
                                .addOptions(SelectOption.of("Suggestion", "suggestion")
                                        .withDescription("Suggest something to the server")
                                        .withEmoji(Emoji.fromUnicode("\ud83d\udcec")))
                                .addOptions(SelectOption.of("Doubts", "doubts")
                                        .withDescription("Doubts & Questions about the server")
                                        .withEmoji(Emoji.fromUnicode("\u2753")))
                                .addOptions(SelectOption.of("Other", "other")
                                        .withDescription("Other reasons")
                                        .withEmoji(Emoji.fromUnicode("\ud83d\udce3")))
                                .build()).queue();

    }
}
