package net.redsierra.Spacio.config;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.mongodb.client.model.Filters.eq;

public class GuildConfig {

    private final Guild guild;
    private final MongoCollection<Document> guildsCollection;
    private final Document document;

    public GuildConfig(Guild guild, MongoCollection<Document> guildsCollection) {
        this.guild = guild;
        this.guildsCollection = guildsCollection;

        Document doc = guildsCollection.find(eq("guildId", guild.getId())).first();
        if (doc == null) {
            doc = new Document("guildId", guild.getId());
            guildsCollection.insertOne(doc);
        }

        this.document = doc;
    }

    public Document getGuildDocument() {
        return document;
    }

    private String getChannelId(String key) {
        return document.getString(key);
    }

    private List<String> getChannelList(String key) {
        Object value = document.get(key);
        if (value instanceof List<?>) {
            List<String> result = new ArrayList<>();
            for (Object o : (List<?>) value) {
                if (o instanceof String) result.add((String) o);
            }
            return result;
        } else if (value instanceof String) {
            return List.of((String) value);
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    public List<Document> getUsers() {
        return (List<Document>) document.getOrDefault("users", new ArrayList<>());
    }

    public String getLogsChannel() {
        return getChannelId(ChannelKey.LOGS.key());
    }

    public String getWelcomeChannel() {
        return getChannelId(ChannelKey.WELCOME.key());
    }

    public String getCommandsChannel() {
        return getChannelId(ChannelKey.COMMANDS.key());
    }

    public String getReportsChannel() {
        return getChannelId(ChannelKey.REPORTS.key());
    }

    public List<String> getXPChannels() {
        return getChannelList(ChannelKey.XP.key());
    }

    public void save() {
        guildsCollection.replaceOne(eq("guildId", guild.getId()), document);
    }
}
