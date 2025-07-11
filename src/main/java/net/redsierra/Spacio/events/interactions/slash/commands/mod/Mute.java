package net.redsierra.Spacio.events.interactions.slash.commands.mod;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
        Member member = event.getOption("user", null, OptionMapping::getAsMember);
        String reason = event.getOption("reason", "No reason provided", OptionMapping::getAsString);
        String rawTime = event.getOption("time", "5m", OptionMapping::getAsString); // Default: 5 minutes

        event.deferReply().setEphemeral(true).queue();
        InteractionHook hook = event.getHook();

        if (guild == null || member == null) {
            hook.editOriginal("❌ Invalid guild or member.").queue();
            return;
        }

        // bot validation

        if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.editOriginal("🛑 You can't mute a moderator.").queue();
            return;
        }

        if (member.isTimedOut()) {
            hook.editOriginal("🔇 This user is already muted. Use `/unmute` to remove the timeout.").queue();
            return;
        }

        try {
            Duration duration = Duration.ofMillis(new TimeParser().parseToMillis(rawTime));
            Instant until = Instant.now().plus(duration);

            guild.timeoutFor(member, duration).reason(reason).queue();

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(member.getUser().getGlobalName() + " muted", null, member.getUser().getAvatarUrl())
                    .addField("Reason", reason, false)
                    .addField("Until", "<t:" + until.getEpochSecond() + ":R>", false)
                    .setTimestamp(Instant.now())
                    .setColor(new BotConfig().getSystemColor())
                    .setFooter("Muted by " + event.getUser().getGlobalName(), event.getUser().getAvatarUrl());

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            hook.editOriginal("✅ User " + member.getAsMention() + " has been muted for " + rawTime + ".").queue();

            // DM al usuario
            member.getUser().openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder dmEmbed = new EmbedBuilder()
                        .setTitle("🔇 You have been muted")
                        .addField("Reason", reason, false)
                        .addField("Until", "<t:" + until.getEpochSecond() + ":R>", false)
                        .setColor(0xffaa00)
                        .setTimestamp(Instant.now())
                        .setFooter("Server: " + guild.getName());

                privateChannel.sendMessageEmbeds(dmEmbed.build()).queue();
            }, failure -> {
                // No se pudo mandar DM
            });

            // Guardar en log
            new InfractionLogger().createLog(
                    member.getId(),
                    reason,
                    event.getUser().getId(),
                    event.getId(),
                    event.getChannel().getId(),
                    "mute",
                    guild
            );

        } catch (IllegalArgumentException e) {
            hook.editOriginal("⏱️ Invalid time format. Use something like `10m`, `1h`, or `30s`.").queue();
        }
    }
}
