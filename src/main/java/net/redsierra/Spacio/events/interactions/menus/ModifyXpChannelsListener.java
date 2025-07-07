package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.config.ChannelKey;

import java.util.ArrayList;
import java.util.List;

public class ModifyXpChannelsListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();

        if (!componentId.startsWith("xp-channel-select-")) return;

        String action = componentId.replace("xp-channel-select-", ""); // "add" o "remove"
        String selectedChannelId = event.getValues().get(0);

        try {
            BotConfig botConfig = new BotConfig();
            GuildConfig guildConfig = botConfig.getGuildConfig(event.getGuild());
            List<String> xpChannels = new ArrayList<>(guildConfig.getXPChannels());

            if ("add".equals(action)) {
                if (xpChannels.contains(selectedChannelId)) {
                    event.reply("❌ That channel is already in the XP system.").setEphemeral(true).queue();
                    return;
                }
                xpChannels.add(selectedChannelId);
            } else if ("remove".equals(action)) {
                if (!xpChannels.contains(selectedChannelId)) {
                    event.reply("❌ That channel is not in the XP system.").setEphemeral(true).queue();
                    return;
                }
                xpChannels.remove(selectedChannelId);
            }

            guildConfig.getGuildDocument().put(ChannelKey.XP.key(), xpChannels);
            guildConfig.save();

            event.reply("✅ Successfully " + (action.equals("add") ? "added" : "removed") + " <#" + selectedChannelId + "> " + (action.equals("add") ? "to" : "from") + " the XP channels.")
                    .setEphemeral(true)
                    .queue();

        } catch (Exception e) {
            e.printStackTrace();
            event.reply("❌ Something went wrong while saving to the database.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
