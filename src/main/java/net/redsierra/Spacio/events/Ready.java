package net.redsierra.Spacio.events;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import org.bson.Document;
import org.reflections.Reflections;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public class Ready extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        BotConfig config = new BotConfig();

        event.getJDA().getGuilds().forEach(guild -> {
            try {
                GuildConfig guildConfig = config.getGuildConfig(guild);
                String commandsChannelId = guildConfig.getCommandsChannel();
                TextChannel channel = guild.getTextChannelById(commandsChannelId);
                if (channel != null && channel.canTalk()) {
                    URL url = this.getClass().getClassLoader().getResource("config.json");
                    assert url != null;
                    URLConnection urlConnection = url.openConnection();
                    if (urlConnection instanceof JarURLConnection) {
                        channel.sendMessage("**Spacio** has been deployed to production using version **" + config.getProjectVersion() + "**.").queue();
                    } else {
                        channel.sendMessage("**Spacio** has been deployed to development (locally) using version **" + config.getProjectVersion() + "**").queue();
                    }
                }
            } catch (Exception e) {
                event.getJDA().retrieveCommands().queue(commands -> {
                    net.dv8tion.jda.api.interactions.commands.Command command = commands.stream()
                            .filter(cmd -> cmd.getName().equals("setupchannels"))
                            .findFirst()
                            .orElse(null);

                    String mention = (command != null) ? command.getAsMention() : "/setupchannels";

                    guild.getTextChannels().stream()
                            .filter(TextChannel::canTalk)
                            .findFirst()
                            .ifPresent(channel -> channel.sendMessage(
                                    "Spacio is now online, but you need to set a commands channel using " + mention
                            ).queue());
                });
            }
        });

        Reflections reflections = new Reflections("net.redsierra.Spacio.events.interactions.slash.commands");

        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> clazz : classes) {
            try {
                Command command = clazz.getDeclaredConstructor().newInstance();
                SlashCommandHandler.registerCommand(command.getName(), command);
            } catch (Exception e) {
                new Spacio().logger.warn("Failed to register command: " + clazz.getSimpleName(), e);
            }
        }

        new Spacio().logger.info("Successfully registered " + SlashCommandHandler.getCommands().size() + " commands.");
    }
}
