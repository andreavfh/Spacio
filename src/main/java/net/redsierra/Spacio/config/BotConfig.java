package net.redsierra.Spacio.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.Guild;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.database.Database;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BotConfig {

    private final MongoDatabase database;
    private final JSONObject file;
    private final MongoCollection<Document> guilds;

    public BotConfig() {
        String mongoUri = getDatabaseUrl();
        if (mongoUri == null || mongoUri.isBlank()) {
            throw new IllegalStateException("MONGO_URI environment variable is not set.");
        }

        this.database = new Database(mongoUri).getDatabase();
        this.file = getConfigFile();
        this.guilds = database.getCollection("guilds");
    }

    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    public String getFactsApiKey() {
        return System.getenv("FACTS_API_KEY");
    }

    public String getDatabaseUrl() {
        return System.getenv("MONGO_URI");
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public JSONObject getConfigFile() {
        try (InputStream inputStream = Spacio.class.getResourceAsStream("/config.json")) {
            Objects.requireNonNull(inputStream, "config.json not found");
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getProjectVersion() {
        JSONObject version = (JSONObject) file.get("version");
        String identifier = (String) version.get("identifier");
        return String.format("%d.%d.%d-%s.%d",
                version.get("major"), version.get("minor"), version.get("patch"), identifier, version.get("build"));
    }

    public Color getSystemColor() {
        Object colorValue = file.get("systemColor");
        if (colorValue == null) {
            return Color.decode("#c49070");
        }
        return Color.decode(colorValue.toString());
    }


    public int getShardCount() {
        String shardCount = System.getenv("SHARD_COUNT");
        if (shardCount == null || shardCount.isEmpty()) return 1;
        try {
            return Integer.parseInt(shardCount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid SHARD_COUNT value: " + shardCount, e);
        }
    }

    public GuildConfig getGuildConfig(Guild guild) {
        return new GuildConfig(guild, this.guilds);
    }
}
