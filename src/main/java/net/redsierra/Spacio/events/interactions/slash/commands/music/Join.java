package net.redsierra.Spacio.events.interactions.slash.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import net.redsierra.Spacio.lavaplayer.GuildMusicManager;
import net.redsierra.Spacio.lavaplayer.PlayerManager;

public class Join extends Command {

    public Join() {
        super.setName("join");
        super.setCategory("music");
        super.setDescription("Ask the bot to enter your voice channel to play music.");
    }

    @Override
    public void execute(SlashCommandInteraction ev) {
        SlashCommandInteractionEvent event = ev.event();

            Member member = event.getMember();
            assert member != null;


            Guild guild = event.getGuild();

            assert guild != null;
            Member selfMember = guild.getSelfMember();

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

               GuildVoiceState voiceState = member.getVoiceState();
               assert voiceState != null;

               if(voiceState.inAudioChannel()) {
                   assert voiceState.getChannel() != null;
                   VoiceChannel channel = voiceState.getChannel().asVoiceChannel();

                   if (selfMember.hasAccess(channel)) {

                        if(musicManager.player.getPlayingTrack() !=null) {
                            event.reply("I'm already playing music, please wait for it to finish. ").setEphemeral(true).queue();
                        } else {
                            guild.getAudioManager().openAudioConnection(channel);
                            event.reply("Joined " + channel.getAsMention()).setEphemeral(true).queue();

                            String str = "Joined " + channel.getName() + " ("+channel.getId()+") in guild " + guild.getName() + " ("+guild.getId()+")";
                            new Spacio().logger.info(str);
                        }

                   } else {
                       event.reply("Ooops, I do not have permission to join that channel.")
                               .setEphemeral(true)
                               .queue();
                   }

               } else {

                   event.reply("You must be in a voice channel to use this command, please join to a voice channel and try again.")
                           .setEphemeral(true)
                           .queue();

        }
    }
}
