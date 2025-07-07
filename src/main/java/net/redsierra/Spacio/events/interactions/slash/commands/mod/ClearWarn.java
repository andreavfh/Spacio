package net.redsierra.Spacio.events.interactions.slash.commands.mod;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

public class ClearWarn extends Command {

    public ClearWarn() {
        super.setName("clearwarn");
        super.setCategory("mod");
        super.setDescription("Returns a success message if the infraction id existed and is removed, otherwise it will send an error message.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {

        SlashCommandInteractionEvent event = ev.event();

            TextInput userId = TextInput.create("warnid", "Warn ID", TextInputStyle.SHORT)
                    .setPlaceholder("The id of the warning to clear.")
                    .build();


            Modal modal = Modal.create("clearwarn", "Clear Warn")
                    .addComponents(ActionRow.of(userId))
                    .build();

            event.replyModal(modal).queue();

    }
}
