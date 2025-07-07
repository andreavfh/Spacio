package net.redsierra.Spacio.events;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.util.WelcomeImageGen;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class MemberJoin extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        BotConfig config;
        config = new BotConfig();
        GuildConfig guildConfig = config.getGuildConfig(event.getGuild());

        TextChannel ch;
        ch = event.getGuild().getTextChannelById(guildConfig.getWelcomeChannel());
        if (ch == null) {
            return;
        }

        InputStream file;
        try {
            file = new WelcomeImageGen().getImage(Objects.requireNonNull(event.getMember()).getUser());
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
        ch.sendFiles(FileUpload.fromData(file, "welcome.png")).queue();
        Guild guild = event.getGuild();
        TextChannel cmds = guild.getTextChannelById(guildConfig.getCommandsChannel());
        assert cmds != null;

        event.getUser().openPrivateChannel().queue((channel) -> {

            channel.sendMessage("Welcome to **" + event.getGuild().getName() + "**").queue();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
                    embed.setTitle("Your new adventure begins here!");
                    embed.setDescription("In **" + guild.getName() + "** we want you to have fun " + event.getUser().getAsMention() + "!\n\n" +
                    "We are glad to have you here. Please read the rules in <#" + event.getGuild().getRulesChannel() + "> and enjoy your stay.\n\n" +
                    "To use commands, you need to execute it on " + cmds.getAsMention() + ".\n" +
                    "If you have any questions, you can open a ticket by using '/ticket'\n" +
                    "You can also check all the commands by using '/help'.\n" +
                    "To get your info roles, use '/roles'.");
                    embed.setColor(new BotConfig().getSystemColor());
                    embed.setFooter("Powered by Spacio");
                    channel.sendMessageEmbeds(embed.build()).queue();
        });
    }
}
