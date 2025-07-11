package net.redsierra.Spacio.events.interactions.slash.commands.mod;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

public class Warn extends Command {


    public Warn() {
        super.setName("warn");
        super.setCategory("mod");
        super.setDescription("Returns a warning if the warned user is not a moderator or if the user is valid. At 4 warnings the user is kicked.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {
        SlashCommandInteractionEvent e = ev.event();

                TextInput userId = TextInput.create("userid", "User Identifier", TextInputStyle.SHORT)
                        .setPlaceholder("The user id of the user to warn")
                        .build();

                TextInput reason = TextInput.create("reason", "Warn Reason", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("The reason of the warn")
                        .setRequiredRange(1, 50)
                        .build();

                Modal modal = Modal.create("warn", "Warn")
                        .addComponents(ActionRow.of(userId), ActionRow.of(reason))
                        .build();

                e.replyModal(modal).queue();

            }

}
