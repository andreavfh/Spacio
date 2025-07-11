package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.List;
import java.util.stream.Collectors;

public class SetupChannelsStringMenu extends ListenerAdapter {
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("setup_channel_select")) return;

        String selectedType = event.getValues().get(0); // welcome, reports, etc.

        List<TextChannel> availableChannels = event.getGuild().getTextChannels().stream()
                .filter(GuildMessageChannel::canTalk)
                .collect(Collectors.toList());

        if (availableChannels.isEmpty()) {
            event.reply("There are no channels where the bot can send messages.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        StringSelectMenu.Builder menu = StringSelectMenu.create("select_channel_for_" + selectedType)
                .setPlaceholder("Choose a channel to assign as " + selectedType);

        for (TextChannel channel : availableChannels) {
            menu.addOption("#" + channel.getName(), channel.getId());
        }

        event.reply("Select the channel you want to set as **" + selectedType + "**:")
                .addActionRow(menu.build())
                .setEphemeral(true)
                .queue();
    }
}