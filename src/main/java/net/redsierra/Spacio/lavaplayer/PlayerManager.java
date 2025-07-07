package net.redsierra.Spacio.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl, InteractionHook hook) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);

                String builder = "Ready! You have successfully added to queue track `" +
                        track.getInfo().title +
                        "` by `" + track.getInfo().author + "`";

                hook.editOriginal(builder).queue();
            }



            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

                if(playlist.isSearchResult()) {
                        AudioTrack track = playlist.getTracks().get(0);
                        musicManager.scheduler.queue(track);

                    String builder = "Ready! You have successfully added to queue track `" +
                            track.getInfo().title +
                            "` by `" + track.getInfo().author + "`";


                        hook.editOriginal(builder).delay(2, TimeUnit.SECONDS).queue();

                } else {

                    for (AudioTrack track : playlist.getTracks()) {
                        musicManager.scheduler.queue(track);

                        String builder = "Ready! You have successfully added to queue playlist with name `" +
                                playlist.getName() +
                                "` with **" + playlist.getTracks().size() + "** tracks";

                        hook.editOriginal(builder).delay(2, TimeUnit.SECONDS).queue();
                    }
                }
            }

            @Override
            public void noMatches() {
                System.out.println("No matches");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                hook.editOriginal("Oops, a fatal error has occurred, `please report the error to a developer`").queue();
            }
        });
    }


    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

}