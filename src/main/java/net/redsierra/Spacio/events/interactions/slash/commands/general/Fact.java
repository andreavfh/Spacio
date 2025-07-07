package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Fact extends Command {

    public Fact() {
        super.setName("fact");
        super.setCategory("misc");
        super.setDescription("Returns a random fact.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {
        SlashCommandInteractionEvent e = ev.event();

        if(this.getFact() != null){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Random fact");
            eb.setDescription(this.getFact());
            eb.setColor(new BotConfig().getSystemColor());
            eb.setFooter("Spacio", e.getJDA().getSelfUser().getAvatarUrl());
            eb.setAuthor(e.getUser().getGlobalName(), e.getUser().getAvatarUrl(), e.getUser().getAvatarUrl());
            e.replyEmbeds(eb.build()).queue();
            e.reply(this.getFact()).queue();
        } else {
            e.reply("An error occurred while trying to get a fact. Report this to developers.").queue();
        }

    }


    private String getFact() {

        HttpURLConnection connection;

        try {
            URL url = new URL("https://api.api-ninjas.com/v1/facts?limit=1");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            String apiKey = System.getenv("FACTS_API_KEY");

            connection.setRequestProperty("X-Api-Key", apiKey);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();


                assert inputStream != null;

                JSONParser parser = new JSONParser();

                JSONArray array =  (JSONArray) parser.parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                JSONObject object = (JSONObject) array.get(0);

                return (String) object.get("fact");

            } else {
                new Spacio().logger.error("GET request not worked");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

}
