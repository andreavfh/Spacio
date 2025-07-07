package net.redsierra.Spacio.events.automod;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AntiBadWords extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Member member = event.getMember();
        assert member != null;

        if(event.getAuthor().isBot()) return;
        if(!event.isFromGuild()) return;
        if(member.hasPermission(Permission.MANAGE_CHANNEL)) return;

        String msg = event.getMessage().getContentRaw();

        JSONParser parser = new JSONParser();
        JSONArray array;
        InputStream inputStream;


        try {
            inputStream = new URL("https://raw.githubusercontent.com/IkariDevGIT/bad_wordlist/main/bad_list.json").openStream();
            array = (JSONArray) parser.parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        Object[] badWords = array.toArray();

        for (Object badWord : badWords) {
            if (msg.contains((String) badWord)) {
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("**No bad words allowed "+member.getAsMention() + ".**").queue();
                break;
            }
        }

    }
}
