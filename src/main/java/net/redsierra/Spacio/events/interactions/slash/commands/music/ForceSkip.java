package net.redsierra.Spacio.events.interactions.slash.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.lavaplayer.GuildMusicManager;
import net.redsierra.Spacio.lavaplayer.PlayerManager;
import java.util.Objects;

public class ForceSkip extends Command {

    public ForceSkip() {
        super.setName("forceskip");
        super.setCategory("music");
        super.setDescription("Skip a song even if it doesn't have enough votes.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {
        SlashCommandInteractionEvent event = ev.event();

            Guild guild = event.getGuild();

            assert guild != null;
            Member selfMember = guild.getSelfMember();
            Member member = event.getMember();
            assert member != null;


            GuildVoiceState voiceState = member.getVoiceState();
            assert voiceState != null;


            if (voiceState.inAudioChannel()) {
                assert selfMember.getVoiceState() != null;

                if (selfMember.getVoiceState().inAudioChannel()) {

                    if (Objects.equals(selfMember.getVoiceState().getChannel(), voiceState.getChannel())) {


                        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);


                        if (musicManager.player.getPlayingTrack() != null) {

                            assert voiceState.getChannel() != null;


                            event.reply("Yes, force skipped `" + musicManager.player.getPlayingTrack().getInfo().title + "`").queue();


                            musicManager.scheduler.nextTrack();

                        } else {
                            event.reply("No songs to skip. Empty queue").queue();
                        }

                    } else {
                        event.reply("We are not on the same voice channel, try using the **join** command ")
                                .setEphemeral(true)
                                .queue();
                    }

                } else {
                    event.reply("I'm not in a voice channel").queue();
                }


            } else {
                event.reply("You need to be on a voice channel to force skip a song")
                        .setEphemeral(true)
                        .queue();
            }
    }


}
