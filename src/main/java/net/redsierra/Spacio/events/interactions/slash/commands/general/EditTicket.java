package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

public class EditTicket extends Command {

    public EditTicket() {
        super.setName("editticket");
        super.setCategory("general");
        super.setDescription("Edit a ticket.");
    }
    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        SlashCommandInteractionEvent event = commandEvent.event();

        if (!event.getChannel().getName().startsWith("ticket-")) {
            event.reply("This command can only be used in a ticket channel.").setEphemeral(true).queue();
            return;
        }

        event.reply("Please select an option below to edit your ticket")
                .addActionRow(
                        StringSelectMenu.create("ticket-edit")
                                .addOptions(SelectOption.of("Topic", "topic")
                                        .withDescription("Change the topic of your ticket")
                                        .withEmoji(Emoji.fromUnicode("\ud83c\udfaf")))
                                .addOptions(SelectOption.of("Archive Ticket", "archive")
                                        .withDescription("Archive your ticket")
                                        .withEmoji(Emoji.fromUnicode("\ud83d\udcc2")))
                                .addOptions(SelectOption.of("Delete Ticket", "delete")
                                        .withDescription("Delete your ticket")
                                        .withEmoji(Emoji.fromUnicode("\ud83d\udcc2")))
                                .build()
                ).queue();
    }
}
