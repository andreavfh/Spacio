package net.redsierra.Spacio.events.interactions.slash.commands.mod;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import org.bson.Document;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Warnings extends Command {

    public Warnings() {
        super.setName("warnings");
        super.setCategory("mod");
        super.setDescription("Returns detailed information about the violations of the user as moderator and the id of the violation if its warnings are greater than 1.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {

        SlashCommandInteractionEvent event = ev.event();

            MongoDatabase db = new BotConfig().getDatabase();

            MongoCollection<Document> c = db.getCollection("warns");
            OptionMapping opt = event.getOption("user");
            assert opt != null;
            assert event.getGuild() != null;

            Member member = event.getGuild().getMemberById(opt.getAsString());
            assert member != null;

            User user = member.getUser();

            List<String[]> l = getUserWarnings(c, user.getId());

            event.deferReply().queue();
            InteractionHook hook = event.getHook();
            hook.sendMessage("Loading...").setEphemeral(true).queue();

            if (l.size() > 0) {

                StringBuilder str = new StringBuilder();
                for (int i = 0; i < l.size(); i++) {
                    str.append(l.get(i)[0]).append(i == l.size() - 1 ? "**.**" : "**,** ");
                }
                StringBuilder str2 = new StringBuilder();
                for (String[] value : l) {
                    str2.append(value[1]).append("\n");
                }

                StringBuilder str3 = new StringBuilder();

                for (String[] strings : l) {
                    str3.append(Objects.requireNonNull(event.getGuild().getMemberById(strings[2])).getAsMention()).append("\n");
                }


                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.decode( (l.size() < 3 ?  "#79abc9" : "#7634fa") ))
                        .setAuthor(user.getName(), null, user.getAvatarUrl())
                        .setTitle("Warning History")
                        .addField("Warning(s)", String.valueOf(l.size()), false)
                        .addField("Reason(s)", str.toString(), false)
                        .addField("Infraction Id(s)", str2.toString(), false)
                        .addField("Moderators", str3.toString(), false)
                        .setTimestamp(Instant.now())
                        .setFooter("To clear a warning, use /clearwarn.");

                hook.editOriginal("Here is the warnings of selected user").setEmbeds(eb.build()).queueAfter(7, TimeUnit.SECONDS);
            } else {
                hook.editOriginal("This user has no warnings. All clear").queue();
        }
    }

    public List<String[]> getUserWarnings(MongoCollection<Document> collection, String userId) {
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
