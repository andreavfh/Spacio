package net.redsierra.Spacio.events.interactions.modals;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
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
import java.util.List;
import java.util.Objects;

public class WarnModal extends ListenerAdapter {

    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        BotConfig config = new BotConfig();
        if (event.getModalId().equals("warn")) {
            try {


                String userId = Objects.requireNonNull(event.getValue("userid")).getAsString();
                MongoDatabase db;
                db = new BotConfig().getDatabase();
                String reason = Objects.requireNonNull(event.getValue("reason")).getAsString();

                try {
                    assert event.getMember() != null;
                    assert event.getGuild() != null;

                    Member m = event.getGuild().getMemberById(userId);

                    // If the user does not exist, reply with a message and return
                    if (m == null) {
                        event.reply("User with id **" + userId + "** does not exist. Please try again.").setEphemeral(true).queue();
                        return;
                    }


                    // If the user is trying to warn themselves, reply with a message and return
                    if (m.getIdLong() == event.getUser().getIdLong()) {
                        event.reply("You can't warn yourself. Please try again.").setEphemeral(true).queue();
                        return;
                    }
                    //

                    // If the user being warned is a staff member, reply with a message and return
                    if (m.hasPermission(Permission.KICK_MEMBERS)) {
                        event.reply("You cannot warn staff members. Please try again.").setEphemeral(true).queue();
                        return;
                    }
                    WarnObject obj = new WarnObject();
                    InfractionLogger logger = new InfractionLogger();

                    EmbedBuilder wEb = new EmbedBuilder()
                            .setAuthor(m.getUser().getGlobalName() + " warned", null, m.getUser().getAvatarUrl())
                            .addField("Reason:", reason, true)
                            .setColor(config.getSystemColor())
                            .setTimestamp(Instant.now())
                            .setFooter("Warned by " + event.getMember().getUser().getGlobalName(), event.getMember().getUser().getAvatarUrl());

                    MongoCollection<Document> collection = db.getCollection("warns");


                    if (getWarns(collection, m.getId()).size() == 3) {
                        collection.deleteMany(new Document("userId", m.getId()));

                        EmbedBuilder eb = new EmbedBuilder()
                                .setAuthor(m.getUser().getGlobalName() + " kicked", null, m.getUser().getAvatarUrl())
                                .addField("Reason:", "Reached 4 warns", true)
                                .setColor(Color.decode("#9aa0e6"))
                                .setTimestamp(Instant.now())
                                .setFooter("Automatic kick", event.getGuild().getSelfMember().getAvatarUrl());

                        event.replyEmbeds(eb.build()).queue();

                    } else {
                        obj.addInfraction(
                                m.getId(),
                                reason,
                                event.getMember().getId(),
                                event.getInteraction().getId(),
                                event.getChannel().getId());
                    }


                    logger.createLog(
                            m.getId(),
                            reason, event.getMember().getId(),
                            event.getInteraction().getId(),
                            event.getChannel().getId(),
                            "Warn",
                            event.getGuild());


                    event.replyEmbeds(wEb.build()).queue();
                } catch (NumberFormatException ex) {
                    event.reply("Oh... You need to select a valid user id. Please try again.").setEphemeral(true).queue();
                } catch (IOException | ParseException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }

            } catch (Exception exception) {
                event.reply("Error occurred while trying to warn user. Please try again.").setEphemeral(true).queue();
                exception.printStackTrace();
            }

        }
    }

    public List<String[]> getWarns(MongoCollection<Document> collection, String userId) {
        List<String[]> warnings = new ArrayList<>();

        Document filter = new Document("userId", userId);

        FindIterable<Document> iterable = collection.find(filter);

        for (Document doc : iterable) {
            String[] warning = {
                    doc.getString("reason"),
                    doc.getString("warnId"),
                    doc.getString("modId")
            };

            warnings.add(warning);
        }
            return warnings;

    }

}