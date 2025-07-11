package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.ChannelKey;
import net.redsierra.Spacio.config.GuildConfig;

import java.util.HashMap;
import java.util.Map;

public class SetupChannelsSelectionMenu extends ListenerAdapter {

    private static final Map<String, String> CHANNEL_TYPE_KEYS = new HashMap<>();

    static {
        CHANNEL_TYPE_KEYS.put("commands", ChannelKey.COMMANDS.key());
        CHANNEL_TYPE_KEYS.put("reports", ChannelKey.REPORTS.key());
        CHANNEL_TYPE_KEYS.put("welcome", ChannelKey.WELCOME.key());
        CHANNEL_TYPE_KEYS.put("logs", ChannelKey.LOGS.key());
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().startsWith("select_channel_for_")) return;

        // Example: select_channel_for_welcome
        String rawType = event.getComponentId().replace("select_channel_for_", "");
        String key = CHANNEL_TYPE_KEYS.get(rawType);

        if (key == null) {
            event.reply("❌ Unknown channel type selected.").setEphemeral(true).queue();
            return;
        }

        String selectedChannelId = event.getValues().get(0); // ID of the selected channel
        Guild guild = event.getGuild();

        if (guild == null) {
            event.reply("❌ This command must be used in a server.").setEphemeral(true).queue();
            return;
        }

        try {
            BotConfig botConfig = new BotConfig();
            GuildConfig guildConfig = botConfig.getGuildConfig(guild);

            guildConfig.getGuildDocument().put(key, selectedChannelId);
            guildConfig.save();

            event.reply("✅ Successfully set **" + rawType + "** channel to <#" + selectedChannelId + ">.")
                    .setEphemeral(true)
                    .queue();

        } catch (Exception e) {
            event.reply("❌ Failed to save channel configuration.")
                    .setEphemeral(true)
                    .queue();
            e.printStackTrace();
        }
    }
}
