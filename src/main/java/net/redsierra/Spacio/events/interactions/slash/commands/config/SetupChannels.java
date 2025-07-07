package net.redsierra.Spacio.events.interactions.slash.commands.config;


import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import net.redsierra.Spacio.config.ChannelKey;

import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;

import java.util.HashMap;
import java.util.Map;

public class SetupChannels extends Command {

    public static final String MODAL_ID = "setup_channels_modal";
    public static final String FIELD_CHANNEL_TYPE = "channel_type";
    public static final String FIELD_CHANNEL_ID = "channel_id";

    public static final Map<String, String> CHANNEL_TYPE_KEYS = new HashMap<>();

    static {
        CHANNEL_TYPE_KEYS.put("commands", ChannelKey.COMMANDS.key());
        CHANNEL_TYPE_KEYS.put("reports", ChannelKey.REPORTS.key());
        CHANNEL_TYPE_KEYS.put("welcome", ChannelKey.WELCOME.key());
        CHANNEL_TYPE_KEYS.put("logs", ChannelKey.LOGS.key());
    }

    public SetupChannels() {
        super.setName("setupchannels");
        super.setDescription("Setup channels for commands, reports, welcome, and logs");
        super.setCategory("config");
    }

    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        StringSelectMenu menu = StringSelectMenu.create("setup_channel_select")
                .setPlaceholder("Select a channel type to set up")
                .addOption("Welcome", "welcome")
                .addOption("Reports", "reports")
                .addOption("Logs", "logs")
                .addOption("Commands", "commands")
                .build();

        commandEvent.event().reply("Please select the type of channel you want to set up:")
                .addActionRow(menu)
                .setEphemeral(true)
                .queue();
    }
}