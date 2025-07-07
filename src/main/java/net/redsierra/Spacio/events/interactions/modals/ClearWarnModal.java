package net.redsierra.Spacio.events.interactions.modals;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
import org.bson.Document;

import java.util.Objects;

public class ClearWarnModal extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if(event.getModalId().equals("clearwarn")) {

            MongoDatabase db = new BotConfig().getDatabase();

            String warnId = Objects.requireNonNull(event.getValue("warnid")).getAsString();
            assert db != null;
            MongoCollection<Document> warns = db.getCollection("warns");
            Document doc = warns.find(new Document("warnId", warnId)).first();

            if(doc != null) {
                warns.deleteOne(new Document("warnId", warnId));
                assert event.getGuild() != null;
                Member m = event.getGuild().getMemberById(doc.getString("userId"));
                assert m != null;
                User user = m.getUser();

                event.reply("Warn with id **" + warnId + "** for " + user.getAsMention() + " successfully removed.")
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("That warn id does not exist.").queue();
            }


        }

    }
}
