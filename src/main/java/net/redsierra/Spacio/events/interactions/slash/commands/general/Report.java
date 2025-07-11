package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import java.time.Instant;


public class Report extends Command {

    public Report() {
        super.setName("report");
        super.setDescription("Report a user who has broken a rule or who has caused problems.");
        super.setCategory("info");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent){

        SlashCommandInteractionEvent event = commandEvent.event();
        OptionMapping infractor = event.getOption("user");
        assert infractor != null;

        User user = infractor.getAsUser();
        assert event.getGuild() != null;
        Member member = event.getGuild().getMemberById(user.getId());
        BotConfig config = new BotConfig();
        GuildConfig guildConfig = config.getGuildConfig(event.getGuild());
        net.dv8tion.jda.api.interactions.commands.Command command = event.getGuild().retrieveCommands()
                .complete()
                .stream()
                .filter(cmd -> cmd.getName().equals("setupchannels"))
                .findFirst()
                .orElse(null);

        String mention = (command != null ? command.getAsMention() : "`/setupchannels`");

        if(guildConfig.getReportsChannel() == null) {
            event.reply("The reports channel has not been set up yet, please setup using " + mention).setEphemeral(true).queue();
            return;
        }

        if (user == event.getUser()) {
            event.reply("You can't report yourself.").setEphemeral(true).queue();
            return;
        }

        if (user.isBot()) {
            event.reply("You can't report a bot. If you think it's a problem, contact the server administrators.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        assert member != null;
        if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply("You can't report a moderator.").setEphemeral(true).queue();
            return;
        }

        try {
            GuildConfig gc = config.getGuildConfig(event.getGuild());

            TextChannel channel = event.getGuild().getTextChannelById(gc.getReportsChannel());
            OptionMapping reason = event.getOption("reason");

            event.reply("Your report has been sent to the moderators.")
                    .setEphemeral(true)
                    .queue();

            assert channel != null;
            assert reason != null;


            OptionMapping option = event.getOption("evidence");

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("New Report!")
                    .setColor(config.getSystemColor())
                    .setDescription("A new report has been sent by " + event.getUser().getAsMention() + ".")
                    .addField("Infractor", user.getAsMention(), false)
                    .addField("Reason", reason.getAsString(), false)
                    .addField("Channel", event.getChannel().getAsMention(), false)
                    .addField("Evidence", (option == null ? "No evidence provided" : ""), false)
                    .setFooter("Report sended by " + event.getUser().getGlobalName(), event.getUser().getAvatarUrl())
                    .setTimestamp(Instant.now());

            if (option != null) {
                eb.setImage(option.getAsAttachment().getUrl());
            }

            channel.sendMessageEmbeds(eb.build()).queue();
        } catch (Exception exception) {
            event.reply("An error occurred while sending the report.").setEphemeral(true).queue();
            exception.printStackTrace();
        }
    }
}
