package net.redsierra.Spacio.events.interactions.modals;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.database.objects.WarnObject;
import net.redsierra.Spacio.util.InfractionLogger;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class WarnModal extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (!event.getModalId().equals("warn")) return;

        BotConfig config = new BotConfig();
        MongoDatabase db = config.getDatabase();

        try {
            String userId = Objects.requireNonNull(event.getValue("userid")).getAsString();
            String reason = Objects.requireNonNull(event.getValue("reason")).getAsString();

            assert event.getMember() != null;
            assert event.getGuild() != null;

            Member m = event.getGuild().getMemberById(userId);

            if (m == null) {
                event.reply("User with ID **" + userId + "** does not exist.").setEphemeral(true).queue();
                return;
            }

            if (m.getIdLong() == event.getUser().getIdLong()) {
                event.reply("You can't warn yourself.").setEphemeral(true).queue();
                return;
            }

            if (m.hasPermission(Permission.KICK_MEMBERS)) {
                event.reply("You can't warn a staff member.").setEphemeral(true).queue();
                return;
            }

            MongoCollection<Document> collection = db.getCollection("warns");
            int currentWarns = getWarns(collection, m.getId()).size();

            event.deferReply(true).queue();

            WarnObject obj = new WarnObject();
            obj.addInfraction(m.getId(), reason, event.getMember().getId(),
                    event.getInteraction().getId(), event.getGuild().getId(), event.getChannel().getId());

            InfractionLogger logger = new InfractionLogger();
            logger.createLog(m.getId(), reason, event.getMember().getId(), event.getInteraction().getId(),
                    event.getChannel().getId(), "Warn", event.getGuild());

            EmbedBuilder warnEmbed = new EmbedBuilder()
                    .setAuthor(m.getUser().getName() + " warned", null, m.getUser().getAvatarUrl())
                    .addField("Reason", reason, false)
                    .setColor(config.getSystemColor())
                    .setTimestamp(Instant.now())
                    .setFooter("Warned by " + event.getMember().getUser().getGlobalName(),
                            event.getMember().getUser().getAvatarUrl());

            event.getChannel().sendMessageEmbeds(warnEmbed.build()).queue();

            TextChannel logChannel = event.getGuild().getTextChannelById(config.getGuildConfig(event.getGuild()).getLogsChannel());
            event.getHook().editOriginal("✅ Warned user " + m.getUser().getAsMention() + " successfully. View log in " + logChannel.getAsMention()).queue();

            if (currentWarns == 3) {
                collection.deleteMany(new Document("userId", m.getId()));
                Instant until = Instant.now().plusSeconds(60 * 10);

                BotConfig botConfig = new BotConfig();
                GuildConfig guildConfig = botConfig.getGuildConfig(event.getGuild());
                Document guildDoc = guildConfig.getGuildDocument();

                List<Document> usersList = (List<Document>) guildDoc.get("users");
                if (usersList == null) {
                    usersList = new ArrayList<>();
                }

                boolean foundUser = false;
                int updatedStrikes = 1;

                Iterator<Document> iterator = usersList.iterator();
                while (iterator.hasNext()) {
                    Document userDoc = iterator.next();
                    if (userDoc.getString("userId").equals(m.getId())) {
                        foundUser = true;
                        int currentStrikes = userDoc.containsKey("strikes") ? userDoc.getInteger("strikes") : 0;
                        updatedStrikes = currentStrikes + 1;

                        if (updatedStrikes >= 3) {
                            userDoc.put("strikes", 0);
                        } else {
                            userDoc.put("strikes", updatedStrikes);
                        }
                        break;
                    }
                }

                if (!foundUser) {
                    Document newUser = new Document()
                            .append("userId", m.getId())
                            .append("strikes", 1)
                            .append("xp", 0)
                            .append("level", 0)
                            .append("name", m.getUser().getName())
                            .append("avatar_url", m.getUser().getAvatarUrl());
                    usersList.add(newUser);
                }

                guildDoc.put("users", usersList);
                guildConfig.save();

                if (updatedStrikes >= 3) {
                    event.getGuild().kick(m, "Exceeded 3 strikes").queue(
                            success -> {
                                EmbedBuilder kickEmbed = new EmbedBuilder()
                                        .setAuthor(m.getUser().getName() + " has been kicked", null, m.getUser().getAvatarUrl())
                                        .addField("Reason", "Exceeded 3 strikes", false)
                                        .setColor(Color.RED)
                                        .setTimestamp(Instant.now())
                                        .setFooter("Enforced by moderation system", event.getGuild().getSelfMember().getAvatarUrl());

                                event.getChannel().sendMessageEmbeds(kickEmbed.build()).queue();
                            },
                            error -> {
                                event.getChannel().sendMessage("❌ Failed to kick the user. Check my permissions.").queue();
                            }
                    );
                    return;
                }

                EmbedBuilder timeoutEmbed = new EmbedBuilder()
                        .setAuthor(m.getUser().getName() + " timed out", null, m.getUser().getAvatarUrl())
                        .addField("Reason", "User reached the maximum number of warnings", false)
                        .addField("Until", "<t:" + until.getEpochSecond() + ":R>", false)
                        .setColor(Color.RED)
                        .setTimestamp(Instant.now())
                        .setFooter("Remember to follow the rules.", event.getGuild().getSelfMember().getAvatarUrl());

                event.getChannel().sendMessageEmbeds(timeoutEmbed.build()).queue(msg -> {
                    m.timeoutFor(java.time.Duration.ofMinutes(10))
                            .reason("Reached 4 warnings")
                            .queue();
                });
            }

        } catch (IOException | ParseException | URISyntaxException | NullPointerException e) {
            event.reply("❌ An error occurred. Please try again.").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }

    private List<String[]> getWarns(MongoCollection<Document> collection, String userId) {
        List<String[]> warnings = new ArrayList<>();
        for (Document doc : collection.find(new Document("userId", userId))) {
            warnings.add(new String[]{
                    doc.getString("reason"),
                    doc.getString("warnId"),
                    doc.getString("modId")
            });
        }
        return warnings;
    }
}
