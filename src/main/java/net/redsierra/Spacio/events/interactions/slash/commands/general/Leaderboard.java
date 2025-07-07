package net.redsierra.Spacio.events.interactions.slash.commands.general;
;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import org.bson.Document;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Leaderboard extends Command {

    public Leaderboard() {
        super.setName("leaderboard");
        super.setCategory("info");
        super.setDescription("Returns the top 5 users with the most XP in the guild.");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        var event = commandEvent.event();
        var guild = event.getGuild();

        BotConfig botConfig = new BotConfig();
        GuildConfig guildConfig = botConfig.getGuildConfig(guild);
        List<Document> users = guildConfig.getUsers();

        users.sort(Comparator.comparingLong(d -> -getXpSafe(d)));

        StringBuilder sb = new StringBuilder();
        AtomicInteger index = new AtomicInteger(0);
        users.stream().limit(5).forEach(user -> {
            String name = user.getString("name");
            long xp = getXpSafe(user);
            sb.append(String.format("> **%d.** %s - %d XP\n", index.incrementAndGet(), name, xp));
        });

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Leaderboard")
                .setAuthor(guild.getSelfMember().getEffectiveName(), null, guild.getSelfMember().getUser().getAvatarUrl())
                .setColor(botConfig.getSystemColor())
                .setTimestamp(Instant.now())
                .setFooter("API: https://spacio.2.us-1.fl0.io", event.getUser().getAvatarUrl());

        embed.setDescription(sb.isEmpty()
                ? "No users with XP found in this server."
                : "Top 5 users with most XP:\n\n" + sb);

        event.deferReply().queue();
        InteractionHook hook = event.getHook();
        hook.editOriginalEmbeds(embed.build()).queue();
    }

    private long getXpSafe(Document doc) {
        Object xpObj = doc.get("xp");
        if (xpObj instanceof Long) {
            return (Long) xpObj;
        } else if (xpObj instanceof Integer) {
            return ((Integer) xpObj).longValue();
        } else {
            return 0L;
        }
    }
}
