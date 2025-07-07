package net.redsierra.Spacio.events.interactions.slash.commands.music;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.lavaplayer.GuildMusicManager;
import net.redsierra.Spacio.lavaplayer.PlayerManager;
import java.util.ArrayList;
import java.util.Objects;

public class Skip extends Command {

    ArrayList<User> voters = new ArrayList<>();

    public Skip() {
        super.setName("skip");
        super.setCategory("music");
        super.setDescription("Skip the current song or vote to skip it.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {

        SlashCommandInteractionEvent event = ev.event();

            Guild guild = event.getGuild();

            assert guild != null;
            Member selfMember = guild.getSelfMember();
            Member member = event.getMember();
            assert  member != null;

                GuildVoiceState voiceState = member.getVoiceState();
                assert voiceState != null;


                if(voiceState.inAudioChannel()) {
                    assert selfMember.getVoiceState() != null;

                    if(selfMember.getVoiceState().inAudioChannel() ) {

                        if (Objects.equals(selfMember.getVoiceState().getChannel(), voiceState.getChannel())) {


                            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);


                            if(musicManager.player.getPlayingTrack() != null) {

                                assert voiceState.getChannel() != null;
                                VoiceChannel channel = voiceState.getChannel().asVoiceChannel();
                                int usersInChannel = channel.getMembers().size();

                                if(usersInChannel < 2) {
                                    guild.getAudioManager().closeAudioConnection();

                                    event.reply("Yes, correctly skipped `" + musicManager.player.getPlayingTrack().getInfo().title + "`").queue();
                                    musicManager.scheduler.nextTrack();

                                } else {
                                    int requiredUsers = (int) Math.ceil(usersInChannel * 0.5);

                                    voters.add(event.getUser());
                                    if(voters.size() >= requiredUsers) {

                                        event.reply("Yes, correctly skipped `" + musicManager.player.getPlayingTrack().getInfo().title + "`").queue();
                                        musicManager.scheduler.nextTrack();
                                        voters.clear();
                                    } else {
                                        String builder = "You have voted to skip" +
                                                "`" +
                                                musicManager.player.getPlayingTrack().getInfo().title +
                                                "`" + " **(" +
                                                voters.size() + "/" +
                                                requiredUsers +
                                                ")**";

                                        event.reply(builder).queue();

                                    }
                                }
                            } else {
                                event.reply("Nothing to skip, empty queue").queue();
                            }

                        } else {
                            event.reply("We are not on the same voice channel, try using the **join** command ")
                                    .setEphemeral(true)
                                    .queue();
                        }
                    } else {
                        event.reply("I need to be on a voice channel to skip a song")
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