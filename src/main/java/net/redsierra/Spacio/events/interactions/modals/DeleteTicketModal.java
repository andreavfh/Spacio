package net.redsierra.Spacio.events.interactions.modals;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class DeleteTicketModal extends ListenerAdapter {
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("delete-ticket")) {
            String ticketId = Objects.requireNonNull(event.getValue("verify-delete")).getAsString();
            String chName = event.getChannel().getName();

            if (!chName.substring(chName.indexOf('-')+1).equals(ticketId)) {
                event.reply("This is not the id of this ticket, please try again.").setEphemeral(true).queue();
            } else {
                assert event.getGuild() != null;
                TextChannel channel = event.getGuild().getTextChannelById(event.getChannel().getId());
                event.reply("Ticket deleted successfully.").setEphemeral(true).queue();
                assert channel != null;
                channel.delete().queue();

            }
        }
    }
}
