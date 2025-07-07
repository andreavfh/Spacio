package net.redsierra.Spacio.events.interactions.slash.commands.mod;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.util.InfractionLogger;
import net.redsierra.Spacio.util.TimeParser;
import java.time.Duration;
import java.time.Instant;

public class Mute extends Command {

    public Mute() {
        super.setName("mute");
        super.setDescription("Mutes a user.");
        super.setCategory("mod");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent) {

        SlashCommandInteractionEvent event = commandEvent.event();
        Guild guild = event.getGuild();
        OptionMapping option = event.getOption("user");
        assert option != null;
        Member member = option.getAsMember();
        assert member != null;

        if (member.getUser().isBot()) {
            event.reply("You can't mute a bot.").setEphemeral(true).queue();
            return;
        }

        if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply("You can't mute a moderator.").setEphemeral(true).queue();
            return;
        }

        if (member.isTimedOut()) {
            event.reply("This user is already muted. If you want to unmute it you can do so by running the unmute command")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        OptionMapping option2 = event.getOption("time");
        OptionMapping option3 = event.getOption("reason");
        assert option3 != null;
        assert guild != null;
        String reason = option3.getAsString();

        try {

            TimeParser parser = new TimeParser();

            Duration duration = (option2 == null) ? Duration.ofSeconds(60*5) : Duration.ofMillis(parser.parseToMillis(option2.getAsString()));

            guild.timeoutFor(member, duration).queue();

            long seconds = duration.getSeconds();
            long HH = seconds / 3600;
            long MM = (seconds % 3600) / 60;
            long SS = seconds % 60;

            String timeLeft = (HH > 0) ? HH + " hours, " + MM + " minutes, " + SS + " seconds" : (MM > 0 ? MM + " minutes, " + SS + " seconds" : SS + " seconds");

            event.replyEmbeds(
                    new EmbedBuilder()
                            .setAuthor(member.getUser().getGlobalName() + " muted", null, member.getUser().getAvatarUrl())
                            .addField("Reason", reason, false)
                            .addField("Time" , timeLeft, false)
                            .setTimestamp(Instant.now())
                            .setColor(new BotConfig().getSystemColor())
                            .setFooter("Muted by " + event.getUser().getGlobalName(), event.getUser().getAvatarUrl())
                            .build()
            ).queue();

            new InfractionLogger().createLog(member.getId(), reason, event.getUser().getId(), event.getId(), event.getChannel().getId(), "mute", guild);

        } catch (IllegalArgumentException exception) {
            event.reply("Invalid time format. Please use the following format: 1h 30m 10s").setEphemeral(true).queue();
            exception.printStackTrace();
        }

    }
}
