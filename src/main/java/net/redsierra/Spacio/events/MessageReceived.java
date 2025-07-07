package net.redsierra.Spacio.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.*;

public class MessageReceived extends ListenerAdapter {

    private static final int COOLDOWN_PERIOD = 60; // segundos
    private final Map<String, Instant> xpCooldown = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()) return;

        Spacio bot = new Spacio();
        Logger logger = bot.logger;
        BotConfig config = new BotConfig();
        Guild guild = event.getGuild();

        // Carga configuración específica del guild
        GuildConfig guildConfig;
        try {
            guildConfig = new GuildConfig(guild, config.getDatabase().getCollection("guilds"));
        } catch (NoSuchElementException e) {
            // Si no hay configuración para el guild, no hacer nada
            return;
        }

        // Verifica que el canal del mensaje esté en los canales XP configurados
        if (!guildConfig.getXPChannels().contains(event.getChannel().getId())) return;

        List<Document> users = guildConfig.getUsers();
        String userId = event.getAuthor().getId();
        Document user = null;

        // Busca usuario en la lista
        for (Document doc : users) {
            if (userId.equals(doc.getString("userId"))) {
                user = doc;
                break;
            }
        }

        // Si no existe, crea el documento de usuario
        if (user == null) {
            user = new Document("userId", userId)
                    .append("xp", 0)
                    .append("level", 0)
                    .append("avatar_url", event.getAuthor().getAvatarUrl())
                    .append("name", event.getAuthor().getName());
            users.add(user);
            guildConfig.getGuildDocument().put("users", users);
            guildConfig.save();
            xpCooldown.put(userId, Instant.now().plusSeconds(COOLDOWN_PERIOD));
            return;
        }

        // Check cooldown
        Instant cooldownExpiry = xpCooldown.get(userId);
        if (cooldownExpiry != null && cooldownExpiry.isAfter(Instant.now())) {
            return;
        }
        xpCooldown.put(userId, Instant.now().plusSeconds(COOLDOWN_PERIOD));

        // Actualiza XP y nivel
        int currentLevel = user.getInteger("level");
        int currentXP = user.getInteger("xp");
        int requiredXP = (currentLevel + 1) * 350;
        int xpGained = new Random().nextInt(6) + 25; // Entre 25 y 30 inclusive
        int totalXP = currentXP + xpGained;

        user.put("xp", totalXP);
        user.put("avatar_url", event.getAuthor().getAvatarUrl());
        user.put("name", event.getAuthor().getName());

        if (totalXP >= requiredXP) {
            user.put("level", currentLevel + 1);
            user.put("xp", 0);
            event.getChannel().sendMessage("WOW " + event.getAuthor().getAsMention() +
                    "! You have leveled up to level " + (currentLevel + 1) + "!").queue();
            logger.info("User {} has leveled up to level {}!", event.getAuthor().getGlobalName(), currentLevel + 1);
        }

        // Actualiza usuario en la lista
        for (int i = 0; i < users.size(); i++) {
            if (userId.equals(users.get(i).getString("userId"))) {
                users.set(i, user);
                break;
            }
        }

        guildConfig.getGuildDocument().put("users", users);
        guildConfig.save();
    }
}
