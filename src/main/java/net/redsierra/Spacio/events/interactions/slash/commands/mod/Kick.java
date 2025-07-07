package net.redsierra.Spacio.events.interactions.slash.commands.mod;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

public class Kick extends Command {

    public Kick() {
        super.setName("kick");
        super.setCategory("mod");
        super.setDescription("It kicks a user that mod selects.");
    }
    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        SlashCommandInteractionEvent ev = commandEvent.getEvent();

        TextInput userId = TextInput.create("userid", "User ID", TextInputStyle.SHORT)
                .setPlaceholder("The user id of the user to kick out of the guild")
                .build();

        TextInput reason = TextInput.create("reason", "Kick Reason", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The reason of the kick action")
                .setRequiredRange(1, 65)
                .build();

        Modal modal = Modal.create("kick", "Kick User")
                .addComponents(ActionRow.of(userId), ActionRow.of(reason))
                .build();

        ev.replyModal(modal).queue();
    }
}
