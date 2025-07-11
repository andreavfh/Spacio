package net.redsierra.Spacio.database.objects;

import com.mongodb.client.MongoDatabase;
import net.redsierra.Spacio.config.BotConfig;
import org.bson.Document;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;


public class WarnObject
{
    public void addInfraction(String userId, String reason, String modId, String warnId, String chId, String guildId) throws IOException, ParseException, URISyntaxException {
        MongoDatabase db = new BotConfig().getDatabase();
        Document warn = new Document("userId", userId)
                .append("reason", reason)
                .append("warnId", warnId)
                .append("guildId", guildId)
                .append("channelId", chId)
                .append("modId", modId);

        db.getCollection("warns").insertOne(warn);
    }
}



