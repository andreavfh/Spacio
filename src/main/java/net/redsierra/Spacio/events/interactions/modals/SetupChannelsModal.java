package net.redsierra.Spacio.events.interactions.modals;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;
import net.redsierra.Spacio.config.ChannelKey;
import net.redsierra.Spacio.util.ChannelValidator;

import java.util.HashMap;
import java.util.Map;

public class SetupChannelsModal extends ListenerAdapter {

    private static final Map<String, String> CHANNEL_TYPE_KEYS = new HashMap<>();

    static {
        CHANNEL_TYPE_KEYS.put("commands", ChannelKey.COMMANDS.key());
        CHANNEL_TYPE_KEYS.put("reports", ChannelKey.REPORTS.key());
        CHANNEL_TYPE_KEYS.put("welcome", ChannelKey.WELCOME.key());
        CHANNEL_TYPE_KEYS.put("logs", ChannelKey.LOGS.key());
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        // Modal ID format: setupchannel-<type>
        if (!event.getModalId().startsWith("setupchannel-")) return;

        String rawType = event.getModalId().replace("setupchannel-", "");
        String key = CHANNEL_TYPE_KEYS.get(rawType);
        if (key == null) {
            event.reply("❌ Invalid channel type. Use one of: commands, reports, welcome, logs.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String channelIdInput = event.getValue("channel_id").getAsString().replaceAll("[<#>]", "");

        String channelInput = event.getValue("channel_id").getAsString();
        TextChannel channel = ChannelValidator.validateTextChannel(event.getGuild(), channelInput);

        if (channel == null) {
            event.reply("❌ Invalid channel or I can't speak in it. Please check the ID and permissions.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        try {
            BotConfig botConfig = new BotConfig();
            GuildConfig guildConfig = botConfig.getGuildConfig(event.getGuild());
            guildConfig.getGuildDocument().put(key, channelIdInput);
            guildConfig.save();

            event.reply("✅ Successfully set **" + rawType + "** channel to <#" + channelIdInput + ">.")
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
