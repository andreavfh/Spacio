package net.redsierra.Spacio.events.interactions.slash.commands.general;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import org.bson.Document;
import java.awt.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

public class WhoIs extends Command {

    public WhoIs() {
        super.setName("whois");
        super.setCategory("info");
        super.setDescription("It returns the information of a user.");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent) {

        SlashCommandInteractionEvent event = commandEvent.event();
        OptionMapping option = event.getOption("user");
        assert option != null;
        Member member = option.getAsMember();
        assert member != null;

        if (member.getUser().isBot()) {

            OffsetDateTime time = member.getTimeJoined();
            String timeJoined = time.getDayOfMonth() + "/" + time.getMonth().getValue() +"/"+ time.getYear();
            OnlineStatus s = member.getOnlineStatus();
            String status = (s == OnlineStatus.ONLINE) ? "Online" : (s == OnlineStatus.IDLE) ? "Idle" : (s == OnlineStatus.DO_NOT_DISTURB) ? "Do not disturb" : "Offline";


            event.replyEmbeds(
                    new EmbedBuilder()
                            .setAuthor(member.getUser().getGlobalName(), null, member.getUser().getAvatarUrl())
                            .setColor(new BotConfig().getSystemColor())
                            .setThumbnail(member.getUser().getAvatarUrl())
                            .setTitle("Bot")
                            .addField("Date added", timeJoined, false)
                            .addField("Identifier", member.getId(), false)
                            .addField("Status", status, false)
                            .addField("Activity", (member.getActivities().isEmpty() ? "No activities" : member.getActivities().get(0).getName()), false)
                            .addField("Role",member.getRoles().get(0).getAsMention(), false)
                            .setTimestamp(Instant.now())
                            .setFooter("Requested by " + event.getUser().getGlobalName(), event.getUser().getAvatarUrl())
                            .build()
            ).setEphemeral(true).queue();
        } else {
            MongoCollection<Document> c;
            c = new BotConfig().getDatabase().getCollection("users");
            String userId = member.getId();
            Document doc = c.find(new Document("userId", userId)).first();

            OffsetDateTime time = member.getTimeJoined();
            String timeJoined = time.getDayOfMonth() + "/" + time.getMonth().getValue() +"/"+ time.getYear();
            String roles = member.getRoles().stream().map(Role::getAsMention).collect(Collectors.joining(", "));

            EmbedBuilder builder = new EmbedBuilder()
                    .setAuthor(member.getUser().getGlobalName(), null, member.getUser().getAvatarUrl())
                    .setColor(Color.decode("#97bcdb"))
                    .setThumbnail(member.getUser().getAvatarUrl())
                    .setTitle("User")
                    .addField("Identifier", member.getId(), false)
                    .addField("Date Joined", timeJoined, false)
                    .addField("Roles", (member.getRoles().isEmpty() ? "No roles" : roles), false)
                    .addField("Nickname", member.getEffectiveName(), false)
                    .addField("Level", (doc == null ? "No level" : String.valueOf(doc.getInteger("level"))), false)
                    .addField("XP", (doc == null ? "No XP" : String.valueOf(doc.getInteger("xp"))), false)
                    .addField("Boosting", (member.getTimeBoosted() == null ? "No" : "Yes"), false);

            if (member.getTimeBoosted() != null) {
                String str = member.getTimeBoosted().getDayOfMonth() + "/" + member.getTimeBoosted().getMonth() + member.getTimeBoosted().getYear();

                builder.addField("Boosting since", str, false);
            }

            event.replyEmbeds(builder.build()).setEphemeral(true).queue();

        }
    }
}
