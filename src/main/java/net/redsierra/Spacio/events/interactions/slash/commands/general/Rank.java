package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.util.RankImageGen;
import org.bson.Document;
import java.io.*;
import java.util.List;

@SuppressWarnings("ALL")
public class Rank extends Command {


    public Rank() {
        super.setName("rank");
        super.setCategory("info");
        super.setDescription("Return a card with your level and experience if you have a level greater than zero, otherwise it will return an error.");

    }

    @Override
    public void execute(SlashCommandInteraction ev) {
        var event = ev.getEvent();
        String userId = event.getUser().getId();

        try {
            BotConfig botConfig = new BotConfig();
            GuildConfig guildConfig = botConfig.getGuildConfig(event.getGuild());
            Document guildDoc = guildConfig.getGuildDocument();

            List<Document> users = (List<Document>) guildDoc.getOrDefault("users", List.of());

            Document userDoc = users.stream()
                    .filter(d -> userId.equals(d.getString("userId")))
                    .findFirst()
                    .orElse(null);

            if (userDoc == null) {
                event.reply("You have no data yet. Please try later.").queue();
                return;
            }

            int level = userDoc.getInteger("level", 0);
            int xp = userDoc.getInteger("xp", 0);

            // Defer reply to acknowledge interaction and get hook
            event.deferReply(true).queue(hook -> {
                try (InputStream f = new RankImageGen().getImage(event.getUser().getName(), event.getUser().getAvatarUrl(), xp, level)) {
                    // Envía el mensaje con archivo como primer mensaje post deferReply
                    hook.sendMessage("Here is your rank card")
                            .addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(f, "rank.png"))
                            .queue();
                } catch (Exception e) {
                    hook.sendMessage("Sorry, something went wrong while generating your rank card.")
                            .queue();
                    e.printStackTrace();
                }
            });


        } catch (Exception e) {
            // Si ocurre un error antes de deferReply, puedes usar reply normalmente
            event.reply("An unexpected error occurred. Please try again later.").queue();
            e.printStackTrace();
        }
    }
}