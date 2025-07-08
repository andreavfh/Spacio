package net.redsierra.Spacio.events.interactions.modals;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ArchiveTicketModal extends ListenerAdapter {
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("archive-ticket")) {
            String ticketId = Objects.requireNonNull(event.getValue("verify-archive")).getAsString();
            String name = event.getChannel().getName();

            if (!name.substring(name.indexOf('-')+1).equals(ticketId)) {
                event.reply("This is not the id of this ticket, please try again.").setEphemeral(true).queue();
            } else {
                TextChannel channel = event.getGuild().getTextChannelById(event.getChannel().getId());
                Role role = event.getGuild().getRolesByName("Support", true).get(0);
                event.reply("Ticket archived successfully.").setEphemeral(true).queue();
                channel.getManager().setName("archived-" + ticketId).queue();
                channel.getManager().setTopic("This ticket has been archived.").queue();
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Ticket Archived")
                        .setAuthor("Ticket System", null, event.getGuild().getSelfMember().getAvatarUrl())
                        .setDescription("This ticket has been archived by " + event.getUser().getAsMention() + ".\n" +
                                "**Ticket ID** " + ticketId + "\n" )
                        .setColor(new BotConfig().getSystemColor());
                channel.sendMessageEmbeds(embed.build()).queue();
                channel.getManager().clearOverridesAdded().queue();
                channel.getManager().putRolePermissionOverride(role.getIdLong(), Permission.MANAGE_CHANNEL.getRawValue(), 0).queue();

            }
        }
    }
}
