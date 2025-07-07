package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.SlashCommandHandler;
import net.redsierra.Spacio.events.interactions.Command;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Help extends Command {

    public Help() {
        super.setName("help");
        super.setCategory("info");
        super.setDescription("It returns the information of all the categories of the commands and their functions," +
                " it can also return the specific information of a command.");
    }

    @Override
    public void execute(SlashCommandInteraction ce) {
        SlashCommandInteractionEvent e = ce.event();
        var u = e.getUser();

        if (e.getOption("command") == null) {
            assert e.getGuild() != null;

            Map<String, String> cats = Map.of(
                "info", "Informative Commands",
                "music", "Music Commands",
                "misc", "Misc Commands",
                "mod", "Moderation Commands"
            );

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("Spacio", null, e.getJDA().getSelfUser().getAvatarUrl())
                    .setColor(new BotConfig().getSystemColor())
                    .setTitle("Spacio Help")
                    .setDescription("**Description**\nHello! I'm Spacio, a bot made by RedSierra. I'm currently in development, but I can do some things. Here is a list of all my commands")
                    .setTimestamp(Instant.now())
                    .setFooter("Requested by " + u.getGlobalName(), u.getAvatarUrl());

            List<net.dv8tion.jda.api.interactions.commands.Command> cmds = e.getGuild().retrieveCommands().complete();

            for (var cat : cats.entrySet()) {
                String key = cat.getKey(), label = cat.getValue();

                List<String> names = SlashCommandHandler.getCommandsByCategory(key)
                        .values().stream().map(Command::getName).toList();

                List<String> mentions = cmds.stream()
                        .filter(c -> names.contains(c.getName()))
                        .map(net.dv8tion.jda.api.interactions.commands.Command::getAsMention)
                        .toList();

                eb.addField(label, String.join(", ", mentions), false);
            }

            e.deferReply().queue();
            InteractionHook hook = e.getHook();
            hook.editOriginal("").setEmbeds(eb.build()).queueAfter(5, TimeUnit.SECONDS);

        } else {
            OptionMapping opt = e.getOption("command");
            assert opt != null;
            String input = opt.getAsString();

            Command cmd = SlashCommandHandler.getCommand(input);
            if (cmd == null) {
                e.reply("Command not found.").queue();
                return;
            }

            assert e.getGuild() != null;
            String id = e.getGuild().retrieveCommands().complete().stream()
                    .filter(c -> c.getName().equals(input))
                    .findFirst().orElseThrow().getId();

            String cat = cap(cmd.getCategory());
            String name = cap(cmd.getName());

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor("Spacio", null, e.getJDA().getSelfUser().getAvatarUrl())
                    .setColor(new BotConfig().getSystemColor())
                    .setTitle(name + " Help")
                    .setDescription(cmd.getDescription())
                    .addField("Name", cmd.getName(), false)
                    .addField("Category", cat, false)
                    .addField("Usage", "</" + input + ":" + id + ">", false)
                    .setTimestamp(Instant.now())
                    .setFooter("Requested by " + u.getGlobalName(), u.getAvatarUrl());

            e.deferReply().queue();
            e.getHook().editOriginal("").setEmbeds(eb.build()).queueAfter(5, TimeUnit.SECONDS);
        }
    }

    private String cap(String s) {
        return (s == null || s.isEmpty()) ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
