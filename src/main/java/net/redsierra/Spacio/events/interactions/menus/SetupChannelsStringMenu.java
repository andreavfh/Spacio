package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class SetupChannelsStringMenu extends ListenerAdapter {
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("setup_channel_select")) return;

        String selectedType = event.getValues().get(0); // welcome, reports, etc.

        TextInput input = TextInput.create("channel_id", "ChannelID", TextInputStyle.SHORT)
                .setPlaceholder("Ej: 123456789012345678")
                .setRequired(true)
                .build();

        Modal modal = Modal.create("setupchannel-" + selectedType, "Setup " + selectedType.toUpperCase() + " Channel")
                .addActionRow(input)
                .build();

        event.replyModal(modal).queue();
    }
}