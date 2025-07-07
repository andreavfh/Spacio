package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

import java.util.Objects;

public class Avatar extends Command {

    public Avatar() {
        super.setName("avatar");
        super.setDescription("Shows the avatar of a user.");
        super.setCategory("info");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent){
        BotConfig config = new BotConfig();
       SlashCommandInteractionEvent event = commandEvent.event();

       if(event.getOption("user") == null) {

           String str = event.getUser().getAvatarUrl() + "?size=4096";
           event.replyEmbeds(
                   new net.dv8tion.jda.api.EmbedBuilder()
                           .setImage(str)
                           .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                           .setColor(java.awt.Color.decode("#878dc9"))
                           .setFooter("Spacio", event.getJDA().getSelfUser().getAvatarUrl())
                           .build()
           ).setEphemeral(true).queue();

       } else {

           User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
           String str = user.getAvatarUrl() + "?size=4096";


           event.replyEmbeds(
                     new net.dv8tion.jda.api.EmbedBuilder()
                            .setImage(str)
                            .setAuthor(user.getName(), null, user.getAvatarUrl())
                            .setColor(config.getSystemColor())
                            .setFooter("Spacio", event.getJDA().getSelfUser().getAvatarUrl())
                            .build()
              ).setEphemeral(true).queue();

       }

    }
}
