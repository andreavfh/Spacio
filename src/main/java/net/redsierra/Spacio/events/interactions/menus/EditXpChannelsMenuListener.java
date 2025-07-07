package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.redsierra.Spacio.config.BotConfig;
import net.redsierra.Spacio.config.GuildConfig;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EditXpChannelsMenuListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("xp-action-select")) {
            String action = event.getValues().get(0); // "add" o "remove"

            List<GuildMessageChannel> eligibleChannels = event.getGuild().getTextChannels()
                    .stream()
                    .filter(GuildMessageChannel::canTalk)
                    .collect(Collectors.toList());

            if (eligibleChannels.isEmpty()) {
                event.reply("❌ No available channels where I can send messages.")
                        .setEphemeral(true).queue();
                return;
            }

            StringSelectMenu.Builder channelMenuBuilder = StringSelectMenu.create("xp-channel-select-" + action)
                    .setPlaceholder("Select a channel to " + (action.equals("add") ? "add" : "remove"));

            GuildConfig guildConfig = new BotConfig().getGuildConfig(event.getGuild());
            List<String> xpChannels = guildConfig.getXPChannels();

            for (GuildMessageChannel channel : eligibleChannels) {
                String channelId = channel.getId();

                // Si es acción "remove", mostrar solo canales que ya están en XP
                if ("remove".equals(action) && !xpChannels.contains(channelId)) {
                    continue;
                }
                // Si es acción "add", mostrar solo canales que no estén ya en XP
                if ("add".equals(action) && xpChannels.contains(channelId)) {
                    continue;
                }

                channelMenuBuilder.addOption("#" + channel.getName(), channelId);
            }

            if (channelMenuBuilder.getOptions().isEmpty()) {
                event.reply("❌ No channels available to " + (action.equals("add") ? "add." : "remove."))
                        .setEphemeral(true)
                        .queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle((action.equals("add") ? "Add" : "Remove") + " XP Channel")
                    .setColor(Color.decode("#878dc9"))
                    .setDescription("Select a channel to " + (action.equals("add") ? "add" : "remove") + " from the XP system.");

            event.replyEmbeds(embed.build())
                    .addActionRow(channelMenuBuilder.build())
                    .setEphemeral(true)
                    .queue();
        }
    }
}
