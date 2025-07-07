package net.redsierra.Spacio.events.interactions.slash.commands.config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

import java.awt.*;

public class EditXpChannels extends Command {

    public EditXpChannels() {
        super.setName("editxpchannels");
        super.setCategory("moderation");
        super.setDescription("Add or remove channels from the XP system.");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        SlashCommandInteractionEvent event = commandEvent.event();

        StringSelectMenu menu = StringSelectMenu.create("xp-action-select")
                .setPlaceholder("Select action")
                .addOption("Add XP channel", "add", "Add a channel to the XP system")
                .addOption("Remove XP channel", "remove", "Remove a channel from the XP system")
                .build();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Edit XP Channels")
                .setColor(Color.decode("#878dc9"))
                .setDescription("Choose an action:");

        event.replyEmbeds(embed.build())
                .addActionRow(menu)
                .setEphemeral(true)
                .queue();
    }
}
