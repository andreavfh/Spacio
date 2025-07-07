package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class EditTicketStringMenu extends ListenerAdapter {
    private static final String EDIT_TICKET_COMPONENT_ID = "ticket-edit";
    private static final String TICKET_NAME_PREFIX = "ticket-";

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(EDIT_TICKET_COMPONENT_ID)) {
            if (!event.getChannel().getName().startsWith(TICKET_NAME_PREFIX)) {
                event.reply("You can only edit tickets.").setEphemeral(true).queue();
                return;
            }
            String val = event.getValues().get(0);
            switch (val) {
                case "topic":
                    event.reply("Please enter the new topic of your ticket").setEphemeral(true)
                            .addActionRow(
                                    StringSelectMenu.create("edit-ticket-type")
                                            .addOptions(SelectOption.of("Bug Report", "bug-report")
                                                    .withDescription("Report a bug in the bot or in the server")
                                                    .withEmoji(Emoji.fromUnicode("\ud83e\udd16")))
                                            .addOptions(SelectOption.of("Suggestion", "suggestion")
                                                    .withDescription("Suggest something to the server")
                                                    .withEmoji(Emoji.fromUnicode("\ud83d\udcec")))
                                            .addOptions(SelectOption.of("Doubts", "doubts")
                                                    .withDescription("Doubts & Questions about the server")
                                                    .withEmoji(Emoji.fromUnicode("\u2753")))
                                            .addOptions(SelectOption.of("Other", "other")
                                                    .withDescription("Other reasons")
                                                    .withEmoji(Emoji.fromUnicode("\ud83d\udce3")))
                                            .build()).queue();
                    break;
                case "archive":
                    event.replyModal(buildTicketModal("archive", "Archive Ticket")).queue();
                    break;
                case "delete":
                    event.replyModal(buildTicketModal("delete", "Delete Ticket")).queue();
                    break;
            }
        }
    }

    private Modal buildTicketModal(String action, String title) {
        TextInput verify = TextInput.create("verify-" + action, "Ticket ID", TextInputStyle.SHORT)
                .setPlaceholder("To verify, enter your ticket ID.")
                .setRequiredRange(1, 20)
                .build();

        return Modal.create(action + "-ticket", title)
                .addComponents(ActionRow.of(verify))
                .build();
    }
}