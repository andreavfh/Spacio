package net.redsierra.Spacio.events.interactions.modals;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.util.InfractionLogger;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class KickModal extends ListenerAdapter {

        public void onModalInteraction(@NotNull ModalInteractionEvent event) {
            Guild g = event.getGuild();

            assert g != null;

            Member owner = g.getOwner();
            assert owner != null;

            if (event.getModalId().equals("kick")) {
                String userId = Objects.requireNonNull(event.getValue("userid")).getAsString();
                String reason = Objects.requireNonNull(event.getValue("reason")).getAsString();

                try {
                    Member m = g.getMemberById(userId);
                    assert m != null;

                    if (m.hasPermission(Permission.MANAGE_CHANNEL) || m.hasPermission(Permission.KICK_MEMBERS)) {
                        event.reply("Heeeey, what are you trying? You cannot kick staff members. Please try again with other user.").setEphemeral(true).queue();
                    }

                    if (m.getIdLong() == event.getUser().getIdLong()) {
                        event.reply("No, no, please no. You can't kick yourself. Please try again with other user.").setEphemeral(true).queue();
                    }

                    if (m.getIdLong() == g.getSelfMember().getIdLong()) {
                        event.reply("Really?. You can't kick me. Please try again with other user.").setEphemeral(true).queue();
                    }

                    if (m.getIdLong() == g.getOwner().getIdLong()) {
                        event.reply("You can't beat god, please don't try. You can't kick the owner of the server. Please try again with other user.").setEphemeral(true).queue();
                    }

                    g.kick(m).reason(reason).queue();
                    event.reply("User " + m.getUser().getAsMention() + " has been kicked from the server.").setEphemeral(true).queue();
                    new InfractionLogger().createLog(m.getId(), reason, event.getUser().getId(), event.getId(), event.getChannel().getId(), "kick", g);

                } catch (Exception e) {
                    event.reply("User with id **" + userId + "** does not exist. Please try again.").setEphemeral(true).queue();
                }
            }
        }
}