package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.SlashCommandHandler;
import net.redsierra.Spacio.events.interactions.Command;

public class BotInfo extends Command {

    public BotInfo() {
        super.setName("botinfo");
        super.setCategory("info");
        super.setDescription("Returns information about the bot and the project.");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent) {

        SlashCommandInteractionEvent event = commandEvent.event();

        String desc = "Spacio is a multifunctional Discord bot, it takes care of moderation and server ranking, as well as a music system.";

        BotConfig config = new BotConfig();

        event.deferReply().queue();
        InteractionHook hook = event.getHook();
        hook.sendMessage("Loading...").setEphemeral(true).queue();
        hook.editOriginal("Here is the bot info").setEmbeds(new EmbedBuilder()
                .setAuthor("Spacio", null, event.getJDA().getSelfUser().getAvatarUrl())
                .setColor(config.getSystemColor())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .addField("Description", desc, false)
                .addField("Project", "Spacio is currently a private project but is developed by wolfdev1", false)
                .addField("Project Version", config.getProjectVersion(), false)
                .addField("GitHub", "[GitHub <:github:1055533215609262100>](https://github.com/wolfdev1)", false)
                .addField("Discord Library", "[JDA " + JDAInfo.VERSION + "]("+JDAInfo.GITHUB+") <:jda:1055541128771928125>", false)
                .addField("Slash Commands", "Currently bot have "+ SlashCommandHandler.getCommands().size() +" commands.", false)
                .addField("Database", "Currently bot use MongoDB", false)
                .setFooter("Spacio", event.getJDA().getSelfUser().getAvatarUrl()).build()).queueAfter(5, java.util.concurrent.TimeUnit.SECONDS);


    }
}
