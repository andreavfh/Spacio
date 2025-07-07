package net.redsierra.Spacio.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.events.interactions.Command;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class SlashCommandHandler extends ListenerAdapter {

    private static final HashMap<String, Command> commands = new HashMap<>();

    public static void registerCommand(String name, Command command) {

        commands.put(name, command);
        new Spacio().logger.info("Registered command " + name);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Spacio bot = new Spacio();
        BotConfig config;
        config = new BotConfig();

        if (event.getGuild() == null) {
            event.reply("This command can only be used in a server.").setEphemeral(true).queue();
            return;
        }

        if (commands.containsKey(event.getName())) {
            GuildConfig guildConfig = config.getGuildConfig(event.getGuild());

            if (guildConfig.getCommandsChannel() == null && !event.getName().equals("setupchannels")) {
                event.reply("To start using Spacio, you need to set-up a commands channel.").setEphemeral(true).queue();
                return;
            }

            if (!event.getChannel().getId().equals(guildConfig.getCommandsChannel())) {
                assert event.getMember() != null;
                if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {

                    event.reply("You can't use commands in this channel.").setEphemeral(true).queue();
                    return;
                }
            }

            Command command = commands.get(event.getName());
            command.execute(new SlashCommandInteraction(event));

            bot.logger.info("Attempting to execute command '{}' from user {} ({}) in guild {} ({})",
                    event.getName(),
                    event.getUser().getName(),
                    event.getUser().getId(),
                    event.getGuild().getName(),
                    event.getGuild().getId()
            );
        }
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }

    public static HashMap<String, Command> getCommandsByCategory(String category) {
        try {
            HashMap<String, Command> commandsByCategory = new HashMap<>();
            for (String commandName : commands.keySet()) {
                Command command = commands.get(commandName);
                if (command.getCategory().equals(category)) {
                    commandsByCategory.put(commandName, command);
                }
            }
            return commandsByCategory;
        } catch (Exception e) {
            new Spacio().logger.error("Error while getting commands by category: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Command getCommand(String name) {
        return commands.get(name);
    }
}