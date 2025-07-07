package net.redsierra.Spacio.events;

import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.util.List;

public class ReadyUpdateAPI extends ListenerAdapter {
    public void updateAPI(MongoDatabase db, Guild guild) {
        List<TextChannel> textChannels = guild.getTextChannels();

        for (TextChannel channel : textChannels) {
            String channelId = channel.getId();
            Document channelDoc = db.getCollection("guildchannels").find(new Document("id", channelId)).first();
            boolean parent = channel.getParentCategory() == null;

            boolean p = !guild.getPublicRole().getPermissions().contains(Permission.VIEW_CHANNEL);
            Document doc = new Document("id", channelId)
                    .append("name", channel.getName())
                    .append("private", p)
                    .append("nsfw", channel.isNSFW())
                    .append("slowmode", channel.getSlowmode())
                    .append("topic", (channel.getTopic() == null ? "null" : channel.getTopic()))
                    .append("position", channel.getPosition())
                    .append("parent_id", (parent ? "null" : channel.getParentCategory().getId()))
                    .append("parent_name", (parent ? "null" : channel.getParentCategory().getName()))
                    .append("type", channel.getType().toString())
                    .append("time_created", String.valueOf(channel.getTimeCreated().toInstant().toEpochMilli()));

            if (channelDoc == null) {
                db.getCollection("guildchannels").insertOne(doc);
            } else if (!channelDoc.isEmpty()) {
                db.getCollection("guildchannels").replaceOne(new Document("id", channelId), doc);
            }
        }
    }
}
