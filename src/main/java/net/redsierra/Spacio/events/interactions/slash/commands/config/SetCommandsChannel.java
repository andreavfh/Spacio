package net.redsierra.Spacio.events.interactions.slash.commands.config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.ChannelKey;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

import java.awt.*;
import java.util.Objects;

public class SetCommandsChannel extends Command {

    public SetCommandsChannel() {
        super.setName("setcommandschannel");
        super.setDescription("Set the channel where commands are allowed.");
        super.setCategory("config");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        SlashCommandInteractionEvent event = commandEvent.event();
        BotConfig config = new BotConfig();

        if (event.getOption("channel") == null) {
            event.reply("You must specify a channel.").setEphemeral(true).queue();
            return;
        }

        MessageChannel selectedChannel = Objects.requireNonNull(event.getOption("channel")).getAsChannel().asTextChannel();

        try {
            GuildConfig guildConfig = config.getGuildConfig(event.getGuild());
            guildConfig.getGuildDocument().put(ChannelKey.COMMANDS.key(), selectedChannel.getId());
            guildConfig.save();

            EmbedBuilder success = new EmbedBuilder()
                    .setTitle("✅ Commands Channel Updated")
                    .setDescription("New commands channel set to: <#" + selectedChannel.getId() + ">")
                    .setColor(Color.decode("#c49070"));

            event.replyEmbeds(success.build()).setEphemeral(true).queue();

        } catch (Exception e) {
            event.reply("Failed to update commands channel.").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }
}
