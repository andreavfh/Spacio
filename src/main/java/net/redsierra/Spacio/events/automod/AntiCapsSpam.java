package net.redsierra.Spacio.events.automod;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
import org.bson.Document;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AntiCapsSpam extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Member member = event.getMember();
        assert member != null;

        if(event.getAuthor().isBot()) return;
        if(!event.isFromGuild()) return;

        if(member.hasPermission(Permission.MANAGE_CHANNEL)) return;

        String msg = event.getMessage().getContentRaw();

            if(msg.length() < 10) return;

            int caps = 0;
            int length = msg.length();

            for (int i = 0; i < msg.length(); i++) {
                char c = msg.charAt(i);
                if (Character.isUpperCase(c)) {
                    caps++;
                }
            }

            int k = (int)(length*(65/100.0f));

            if(caps >= k) {

                event.getMessage().delete().queue();
                MongoCollection<Document> collection;

                collection = new BotConfig().getDatabase().getCollection("autoWarns");

                Document filter = new Document("userId", member.getId());

                FindIterable<Document> iterable = collection.find(filter);
                ArrayList<String[]> autoWarns = new ArrayList<>();

                for (Document doc : iterable) {
                    String[] warning = {
                            doc.getString("reason"),
                            doc.getString("warnId"),
                            doc.getString("modId")
                    };
                    autoWarns.add(warning);
                }

                    int i = (autoWarns.size() + 1) * 5;

                    switch (autoWarns.size()) {
                        case 3: {
                            member.timeoutFor(i, TimeUnit.MINUTES).queue();
                        }
                        case 7: {
                            member.timeoutFor(i, TimeUnit.MINUTES).queue();
                            collection.find(filter).forEach(collection::deleteOne);
                        }
                }

                Document warn = new Document("userId", member.getId())
                        .append("type", "CAPS_SPAM")
                        .append("warnId", event.getMessageId())
                        .append("channelId", event.getChannel().getId());

                collection.insertOne(warn);
                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor(member.getUser().getGlobalName() + " warned", null, member.getUser().getAvatarUrl())
                        .addField("Reason", "Caps Spam (Caps > 70% of message)", true)
                        .setColor(Color.decode("#a9bbcf"))
                        .setTimestamp(Instant.now())
                        .setFooter("Automod", event.getJDA().getSelfUser().getAvatarUrl());

                event.getChannel().asTextChannel().sendMessageEmbeds(embed.build()).queue();

            }
    }
}
