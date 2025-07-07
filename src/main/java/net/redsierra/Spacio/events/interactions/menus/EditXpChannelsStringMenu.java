package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EditXpChannelsStringMenu extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Handle selection of add/remove XP channel
        if (!event.getComponentId().equals("edit-xp-channels")) return;

        String action = event.getValues().get(0);

        if (action.equals("add")) {
            event.reply("You selected: **Add XP channel**. Please select the channel in the next menu.")
                    .setEphemeral(true)
                    .queue();

            // Here you would trigger/send the next channel selection menu for adding

        } else if (action.equals("remove")) {
            event.reply("You selected: **Remove XP channel**. Please select the channel in the next menu.")
                    .setEphemeral(true)
                    .queue();

            // Here you would trigger/send the next channel selection menu for removing
        }
    }
}
