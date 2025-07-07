package net.redsierra.Spacio.events.interactions.slash.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.lavaplayer.PlayerManager;
import java.net.URL;
import java.util.Objects;

public class Play extends Command {

    public Play() {
        super.setName("play");
        super.setCategory("music");
        super.setDescription("Add a song to the queue for the bot to play it.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {
        SlashCommandInteractionEvent event = ev.event();

        Member member = event.getMember();
            assert member != null;


            Guild guild = event.getGuild();

            assert guild != null;
            Member selfMember = guild.getSelfMember();

                GuildVoiceState voiceState = member.getVoiceState();
                assert voiceState != null;


                if(voiceState.inAudioChannel()) {
                    assert selfMember.getVoiceState() != null;

                    if(selfMember.getVoiceState().inAudioChannel() ) {

                        if (Objects.equals(selfMember.getVoiceState().getChannel(), voiceState.getChannel())) {
                            OptionMapping option = event.getOption("song");
                            String req;
                            try {
                                assert option != null;
                                URL url = new URL(option.getAsString());
                                url.toURI();


                                req = option.getAsString();
                            } catch (Exception e) {
                                req = "ytsearch:" + option.getAsString();
                            }

                            PlayerManager.getInstance().loadAndPlay(
                                    event.getChannel().asTextChannel(),
                                    req,
                                    event.getHook()
                            );

                            event.reply("Loading your song...")
                                    .setEphemeral(false)
                                    .queue();

                        } else {
                            event.reply("We are not on the same voice channel, try using the **join** command ")
                                    .setEphemeral(true)
                                    .queue();
                        }
                    } else {
                        event.reply("I need to be on a voice channel to play music")
                                .setEphemeral(true)
                                .queue();
                    }
                } else {
                    event.reply("You must be in a voice channel to use this command.")
                            .setEphemeral(true)
                            .queue();
                }


    }
}
