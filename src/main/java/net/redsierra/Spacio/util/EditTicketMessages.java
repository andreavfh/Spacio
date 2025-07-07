package net.redsierra.Spacio.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.redsierra.Spacio.config.BotConfig;

public class EditTicketMessages {
    public EmbedBuilder createEmbed(String description, StringSelectInteractionEvent event) {
        assert event.getGuild() != null;
        TextChannel ch = event.getGuild().getTextChannelById(event.getChannel().getId());
        assert ch != null;
        ch.getManager().setName("ticket-" + event.getInteraction().getId()).queue();
        return new EmbedBuilder()
                .setAuthor(event.getUser().getGlobalName() + " changed ticket topic", null, event.getUser().getAvatarUrl())
                .setDescription(description)
                .setColor(new BotConfig().getSystemColor())
                .setFooter("Powered by Spacio");
    }
    public void setTopicAndSendMessages(TextChannel channel, String topic, String replyMessage, String description, StringSelectInteractionEvent event) {
        channel.getManager().setTopic("This ticket is about " + topic.toLowerCase() + ".").queue();
        event.reply("Ticket topic changed to `" + replyMessage + "`").setEphemeral(true).queue();
        channel.sendMessageEmbeds(createEmbed(description, event).build()).queue();
    }
}
