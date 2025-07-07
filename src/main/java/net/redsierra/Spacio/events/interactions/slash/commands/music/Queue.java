package net.redsierra.Spacio.events.interactions.slash.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.lavaplayer.GuildMusicManager;
import net.redsierra.Spacio.lavaplayer.PlayerManager;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class Queue extends Command {
    public Queue() {
        super.setName("queue");
        super.setCategory("music");
        super.setDescription("Ask the bot for the queue, it can be empty or with a list of tracks.");
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

                        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
                        LinkedBlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

                        if(queue.isEmpty()) {
                            event.reply("The queue is empty, add a song to start.").queue();
                        } else {

                            EmbedBuilder embedBuilder = new EmbedBuilder()
                                    .setTitle("Song Queue")
                                    .setColor(Color.decode("#6281c4"))
                                    .addField("Now Playing", musicManager.player.getPlayingTrack().getInfo().title, false)
                                    .setTimestamp(Instant.now())
                                    .setFooter("Requested by" + member.getUser().getGlobalName(), member.getUser().getAvatarUrl());

                            StringBuilder stringBuilder = new StringBuilder();
                            for(AudioTrack track : queue) {
                                        stringBuilder
                                        .append("**#")
                                        .append(track.getPosition() +1)
                                        .append("** ")
                                        .append(track.getInfo().title)
                                        .append("\n");
                            }

                            embedBuilder.setDescription(stringBuilder.toString());

                            event.replyEmbeds(embedBuilder.build()).queue();

                        }

                    } else {
                        event.reply("We are not on the same voice channel, try using the **join** command ")
                                .setEphemeral(true)
                                .queue();
                    }
                } else {
                    event.reply("I need to be on a voice channel to send you queue. Try using the **join** command")
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