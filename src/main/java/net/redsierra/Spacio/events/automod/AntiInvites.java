package net.redsierra.Spacio.events.automod;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AntiInvites extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Member member = event.getMember();
        assert member != null;

        if(event.getAuthor().isBot()) return;
        if(!event.isFromGuild()) return;
        if(member.hasPermission(Permission.MANAGE_CHANNEL)) return;

        String regex = "discord.gg/\\w+|discord.com/invite/\\w+|discordapp.com/invite/\\w+";

        if(event.getMessage().getContentRaw().matches(regex)) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessage("**No invites allowed "+member.getAsMention() + ".**").queue();}

    }
}
