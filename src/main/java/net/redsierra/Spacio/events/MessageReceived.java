package net.redsierra.Spacio.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MessageReceived extends ListenerAdapter {

    private static final int COOLDOWN_SECONDS = 60;
    private static final int XP_MIN_GAIN = 25;
    private static final int XP_MAX_GAIN = 30;
    private static final String USERS_KEY = "users";
    private static final String COLLECTION_NAME = "guilds";

    private final Map<String, Instant> xpCooldown = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()) return;

        BotConfig botConfig = Spacio.getInstance().getBotConfig();

        final Guild guild = event.getGuild();
        final String channelId = event.getChannel().getId();
        final String userId = event.getAuthor().getId();

        GuildConfig guildConfig;
        try {
            guildConfig = new GuildConfig(guild, botConfig.getDatabase().getCollection(COLLECTION_NAME));
        } catch (NoSuchElementException e) {
            return; // Config no existe
        }

        if (!guildConfig.getXPChannels().contains(channelId)) return;

        Instant cooldownEnd = xpCooldown.get(userId);
        if (cooldownEnd != null && cooldownEnd.isAfter(Instant.now())) return;
        xpCooldown.put(userId, Instant.now().plusSeconds(COOLDOWN_SECONDS));

        List<Document> users = guildConfig.getUsers();
        Document user = users.stream()
                .filter(doc -> userId.equals(doc.getString("userId")))
                .findFirst()
                .orElse(null);

        boolean isNewUser = false;
        if (user == null) {
            user = new Document("userId", userId)
                    .append("xp", 0)
                    .append("level", 0);
            users.add(user);
            isNewUser = true;
        }

        updateUserMeta(user, event);
        boolean leveledUp = updateXPAndLevel(user, event);

        // Persist only if user is new or leveled up
        if (isNewUser || leveledUp) {
            guildConfig.getGuildDocument().put(USERS_KEY, users);
            guildConfig.save();
        }
    }

    private void updateUserMeta(Document user, MessageReceivedEvent event) {
        user.put("avatar_url", event.getAuthor().getAvatarUrl());
        user.put("name", event.getAuthor().getName());
    }

    private boolean updateXPAndLevel(Document user, MessageReceivedEvent event) {
        int currentXP = user.getInteger("xp", 0);
        int currentLevel = user.getInteger("level", 0);
        int gainedXP = ThreadLocalRandom.current().nextInt(XP_MIN_GAIN, XP_MAX_GAIN + 1);
        int newXP = currentXP + gainedXP;
        int requiredXP = (currentLevel + 1) * 350;

        if (newXP >= requiredXP) {
            user.put("level", currentLevel + 1);
            user.put("xp", 0);

            event.getChannel().sendMessage(
                    String.format("🎉 %s has leveled up to level %d!",
                            event.getAuthor().getAsMention(),
                            currentLevel + 1)
            ).queue();

            Spacio.getInstance().getLogger().info("User {} leveled up to {}",
                    event.getAuthor().getGlobalName(), currentLevel + 1);

            return true;
        } else {
            user.put("xp", newXP);
            return true;
        }
    }
}
